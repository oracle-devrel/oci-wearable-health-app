import * as React from "react";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import Typography from "@mui/material/Typography";
import Modal from "@mui/material/Modal";
import TextField from "@mui/material/TextField";
import { linkDevice } from "../../../apis";
import { useMutation } from "@tanstack/react-query";
import { useAuthToken } from "../../../hooks";
import SnackBar from "./../../../components/CustomizedBottomCenterSnackbar";
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

const LinkDeviceModal: React.FC<{
  onSubmitSuccessCallback?: () => void;
}> = ({ onSubmitSuccessCallback }) => {
  const { userName } = useAuthToken();
  const [open, setOpen] = React.useState(false);
  const handleOpen = () => setOpen(true);
  const handleClose = () => setOpen(false);
  const { mutate, isLoading, data, isSuccess } = useMutation(linkDevice, {
    onSuccess: async (res) => {
      if (
        !!onSubmitSuccessCallback &&
        typeof onSubmitSuccessCallback === "function"
      ) {
        try {
          await onSubmitSuccessCallback();
        } catch (e) {
          console.warn(e);
        }
      }
      if (!res?.data?.response?.errorMessage) {
        handleClose();
      }
    },
  });

  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const data = new FormData(event.currentTarget);
    mutate({
      username: userName as string,
      serialNo: data.get("serialNo") as string,
    });
  };
  return (
    <>
      <Button disabled={isLoading} variant="outlined" onClick={handleOpen}>
        {isLoading ? "Linking device" : "Link device"}
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
              Link a device
            </Typography>

            <TextField
              InputLabelProps={{
                shrink: true,
              }}
              margin="normal"
              required
              fullWidth
              id="serialNo"
              label={"Serial number"}
              name="serialNo"
              autoFocus
            />
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
      {isSuccess && !data?.data?.response?.errorMessage && (
        <SnackBar
          severity="success"
          msg="Successfully linked a device to your account!"
        />
      )}
      {!!data?.data?.response?.errorMessage && (
        <SnackBar severity="error" msg={data?.data?.response?.errorMessage} />
      )}
    </>
  );
};
export default LinkDeviceModal;
