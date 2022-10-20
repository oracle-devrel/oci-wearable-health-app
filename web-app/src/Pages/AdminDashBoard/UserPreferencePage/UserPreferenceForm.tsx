import React from "react";
import Button from "@mui/material/Button";
import TextField from "@mui/material/TextField";
import Grid from "@mui/material/Grid";
import Box from "@mui/material/Box";
import { addUserPref, UserPreferencesPayload } from "./../../../apis";
import { useMutation } from "@tanstack/react-query";
import { diplayStringMap } from "./../../../constants";
import { useAuthToken, useClearQueryCache } from "../../../hooks";
import { Typography } from "@mui/material";
import SnackBar from "./../../../components/CustomizedBottomCenterSnackbar";
import Select, { buildMenuItems } from "./../../../components/SelectDropdown";
export const fieldNames: (keyof UserPreferencesPayload)[] = [
  "alertThresholdForBPHigh",
  "alertThresholdForBPLow",
  "alertThresholdForHearRate",
  "alertThresholdForSPO2",
  "alertThresholdForTemp",
  "emergencyEmail",
  "emergencyMobile",
  "notificationFrequency",
  "preferedAlertChannel",
];
export const getUserPreferencePayload = (formData: FormData) => {
  return fieldNames.reduce((pre, cur) => {
    const currentFieldValue = formData.get(cur);
    return !!currentFieldValue ? { ...pre, [cur]: currentFieldValue } : pre;
  }, {} as UserPreferencesPayload);
};
/**
 *
 * @param onSuccessCallback optional callback that will be invoked when form submit api call succeeds
 * @returns
 */
const UserPreferenceForm: React.FC<{
  onSuccessCallback?: () => void;
}> = ({ onSuccessCallback }) => {
  const { mutate, isLoading, isSuccess } = useMutation(addUserPref, {
    onSuccess: () => {
      if (!!onSuccessCallback && typeof onSuccessCallback === "function") {
        try {
          onSuccessCallback();
        } catch (e) {
          console.warn(e);
        }
      }
    },
  });

  useClearQueryCache();

  const { userName } = useAuthToken();
  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const data = new FormData(event.currentTarget);
    mutate({
      username: userName as string,
      userPreferences: getUserPreferencePayload(data),
    });
  };

  return (
    <>
      <Box component="form" noValidate onSubmit={handleSubmit} sx={{ mt: 3 }}>
        <Grid container spacing={2} justifyContent={"center"}>
          <Typography>Create user preferences</Typography>
          {fieldNames.map((name, i) => {
            if (name === "preferedAlertChannel") {
              return (
                <Grid item xs={12} key={i}>
                  <Select
                    fieldName={name}
                    label={diplayStringMap[name]}
                    menuItems={buildMenuItems(
                      [
                        { text: "Email", value: "Email" },
                        { text: "Mobile", value: "Mobile", disabled: true },
                      ],
                      "Please choose preffered alert channel"
                    )}
                  />
                </Grid>
              );
            }
            return (
              <Grid item xs={12} key={i}>
                <TextField
                  required
                  fullWidth
                  id={name}
                  label={diplayStringMap[name]}
                  name={name}
                />
              </Grid>
            );
          })}
        </Grid>
        <Button
          disabled={isLoading}
          type="submit"
          fullWidth
          variant="contained"
          sx={{ mt: 3, mb: 2 }}
        >
          Create user preferences
        </Button>
      </Box>
      {isSuccess && (
        <SnackBar
          severity="success"
          msg="Successfully created user preferences!"
        />
      )}
    </>
  );
};
export default UserPreferenceForm;
