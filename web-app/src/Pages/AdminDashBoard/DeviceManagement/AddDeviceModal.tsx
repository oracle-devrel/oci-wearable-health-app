import * as React from "react";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import Typography from "@mui/material/Typography";
import Modal from "@mui/material/Modal";
import TextField from "@mui/material/TextField";
import { addDevice, Device } from "./../../../apis";
import { useMutation } from "@tanstack/react-query";
import { useAuthToken } from "../../../hooks";
import dayjs from "dayjs";
import SnackBar from "./../../../components/CustomizedBottomCenterSnackbar";
import Select, { buildMenuItems } from "./../../../components/SelectDropdown";

const style = {
  position: "absolute" as "absolute",
  top: "50%",
  left: "50%",
  transform: "translate(-50%, -50%)",
  width: "60vw",
  bgcolor: "background.paper",
  boxShadow: 24,
  p: 4,
};
const formFieldNames: (keyof Device)[] = [
  "deviceSerialNumber",
  "deviceType",
  "activationDate",
];
const diplayStringMap: Record<keyof Device, string> = {
  activationDate: "Activation Date",
  deviceSerialNumber: "Device serial number",
  deviceType: "Device type",
  status: "Status",
};
const getDevicePayload = (formData: FormData) => {
  return formFieldNames.reduce((pre, cur) => {
    const currentFieldValue = formData.get(cur);
    return !!currentFieldValue
      ? {
          ...pre,
          [cur]:
            cur === "activationDate" &&
            dayjs(currentFieldValue as string).isValid()
              ? dayjs(currentFieldValue as string).toISOString()
              : currentFieldValue,
        }
      : pre;
  }, {} as Device);
};
const AddDeviceModal: React.FC<{
  onSubmitSuccessCallback?: () => void;
}> = ({ onSubmitSuccessCallback }) => {
  const { userName } = useAuthToken();
  const [open, setOpen] = React.useState(false);
  const handleOpen = () => setOpen(true);
  const handleClose = () => setOpen(false);
  const { mutate, isLoading, isError, error, isSuccess } = useMutation(
    addDevice,
    {
      onSuccess: async () => {
        if (
          !!onSubmitSuccessCallback &&
          typeof onSubmitSuccessCallback === "function"
        ) {
          try {
            onSubmitSuccessCallback();
          } catch (e) {
            console.warn(e);
          }
        }
        handleClose();
      },
    }
  );
  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const data = new FormData(event.currentTarget);
    mutate({
      username: userName as string,
      device: { ...getDevicePayload(data), status: "ACTIVE" },
    });
  };
  return (
    <>
      {isError && (
        <Typography variant="inherit" color="red">
          {`Failed to add a new device. Error: ${error}`}
        </Typography>
      )}
      <Button disabled={isLoading} variant="outlined" onClick={handleOpen}>
        {isLoading ? "Adding device" : "Add a new device"}
      </Button>
      <Modal
        open={open}
        onClose={handleClose}
        aria-labelledby="modal-modal-title"
        aria-describedby="modal-modal-description"
      >
        <>
          <Box onSubmit={handleSubmit} component="form" sx={style}>
            <Typography noWrap variant="h5" color="primary" align="center">
              Add a device
            </Typography>

            {formFieldNames.map((name, index) => {
              if (name === "activationDate") {
                return (
                  <TextField
                    key={`${index}${name}`}
                    margin="normal"
                    id={name}
                    label={diplayStringMap[name]}
                    type="date"
                    name={name}
                    defaultValue={dayjs(new Date())
                      .format("YYYY-MM-DD")
                      .toString()}
                    fullWidth
                    InputLabelProps={{
                      shrink: true,
                    }}
                  />
                );
              }
              if (name === "deviceType") {
                return (
                  <Select
                    key={`${index}${name}`}
                    fieldName={name}
                    label={diplayStringMap[name]}
                    menuItems={buildMenuItems(
                      [
                        { text: "Watch", value: "Watch" },
                        { text: "Fitness Tracker", value: "Fitness Tracker" },
                        { text: "Body Sensor", value: "Body Sensor" },
                      ],
                      "Please choose a device type"
                    )}
                    sx={{ width: "100%" }}
                  />
                );
              }

              return (
                <TextField
                  key={`${index}${name}`}
                  InputLabelProps={{
                    shrink: true,
                  }}
                  margin="normal"
                  required
                  fullWidth
                  id={name}
                  label={diplayStringMap[name]}
                  name={name}
                  autoFocus={index === 0}
                />
              );
            })}
            <Button type="submit" variant="contained" sx={{ mt: 3, mb: 2 }}>
              Submit
            </Button>
            <Button
              onClick={handleClose}
              variant="outlined"
              sx={{ mt: 3, mb: 2 }}
            >
              Cancel
            </Button>
          </Box>
        </>
      </Modal>
      {isSuccess && (
        <SnackBar msg={`Successfully added a new device!`} severity="success" />
      )}
    </>
  );
};
export default AddDeviceModal;
