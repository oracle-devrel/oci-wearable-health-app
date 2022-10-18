import * as React from "react";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Typography from "@mui/material/Typography";
import { UserPreferencesPayload } from "../../../apis/addUserPref";
import { diplayStringMap } from "./../../../constants";
import { Box, Stack } from "@mui/system";
import { Button, TextField } from "@mui/material";
import { fieldNames, getUserPreferencePayload } from "./UserPreferenceForm";
import { updateUserPref } from "./../../../apis/";
import { useMutation } from "@tanstack/react-query";
import { useAuthToken } from "../../../hooks";
import SnackBar from "./../../../components/CustomizedBottomCenterSnackbar";
import PreferredAlertChannelSelect from "./../PreferredAlertChannelSelect";
const PreferenceDisplayTable: React.FC<{
  userPreference: UserPreferencesPayload;
  onUpdateSuccessCallback?: () => void;
}> = ({ userPreference, onUpdateSuccessCallback }) => {
  const [isEditMode, setIsEditMode] = React.useState(false);
  const formRef = React.useRef<HTMLFormElement>();
  const { mutate, isLoading, isSuccess } = useMutation(updateUserPref, {
    onSuccess: () => {
      if (
        !!onUpdateSuccessCallback &&
        typeof onUpdateSuccessCallback === "function"
      ) {
        try {
          onUpdateSuccessCallback();
        } catch (e) {
          console.warn(e);
        }
      }
    },
  });
  // useClearQueryCache();
  const { userName } = useAuthToken();
  return (
    <>
      <Typography component="h2" variant="h6" color="primary" gutterBottom>
        {"User Preference details"}
      </Typography>
      <Box ref={formRef} component="form">
        <Table size="small">
          <TableHead>
            <TableRow>
              <TableCell>
                <Typography>Preference item</Typography>
              </TableCell>
              <TableCell>
                <Typography>Preference value</Typography>
              </TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {fieldNames.map((key, index) => (
              <TableRow sx={{ height: "2rem" }} key={`${key}-${index}`}>
                <TableCell width="50%">
                  {diplayStringMap[key as keyof UserPreferencesPayload]}
                </TableCell>
                <TableCell width="50%">
                  {isEditMode ? (
                    key ===
                    ("preferedAlertChannel" as keyof UserPreferencesPayload) ? (
                      <PreferredAlertChannelSelect
                        fieldName={key}
                        // label="Preferred alert channel"
                        defaultValue={userPreference[key] as string}
                      />
                    ) : (
                      <TextField
                        required
                        id={key}
                        defaultValue={userPreference[key]}
                        size="small"
                        name={key}
                      />
                    )
                  ) : (
                    <Typography height="2.5rem">
                      {userPreference[key]}
                    </Typography>
                  )}
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </Box>
      <Stack
        mt="1rem"
        direction="row"
        justifyContent="center"
        alignItems="center"
      >
        <Button
          onClick={() => {
            if (isEditMode) {
              mutate({
                username: userName as string,
                userPreferences: getUserPreferencePayload(
                  new FormData(formRef?.current as HTMLFormElement)
                ),
              });
            } else {
              setIsEditMode(!isEditMode);
            }
          }}
          disabled={isEditMode && isLoading}
          variant={isEditMode ? "contained" : "outlined"}
        >
          {isEditMode ? (isLoading ? "Updating preference" : "Save") : "Edit"}
        </Button>
        {isEditMode && (
          <Button
            onClick={() => {
              setIsEditMode(!isEditMode);
            }}
          >
            {isEditMode ? "Cancel" : "Edit"}
          </Button>
        )}
      </Stack>

      {isSuccess && (
        <SnackBar
          severity="success"
          msg={"Successfully updated user preferences!"}
        />
      )}
    </>
  );
};
export default PreferenceDisplayTable;
