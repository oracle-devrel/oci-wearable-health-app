import * as React from "react";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import Typography from "@mui/material/Typography";
import Modal from "@mui/material/Modal";

const style = {
  position: "absolute" as "absolute",
  top: "50%",
  left: "50%",
  transform: "translate(-50%, -50%)",
  width: "40vw",
  bgcolor: "background.paper",
  boxShadow: 24,
  p: 4,
  display: "flex",
  flexDirection: "column",
};

const AddDeviceModal: React.FC<{
  expiration: number;
  confirmHandler?: () => void;
}> = ({ confirmHandler, expiration }) => {
  const [countDown, setCountDown] = React.useState(expiration / 1000);

  React.useEffect(() => {
    let handle = setInterval(() => {
      setCountDown((countDown) => countDown - 1);
    }, 1000);
    if (countDown <= 0) {
      clearInterval(handle);
    }
    return () => clearInterval(handle);
    // );
  }, [setCountDown, expiration, countDown]);

  return (
    <>
      <Modal
        open={true}
        aria-labelledby="modal-modal-title"
        aria-describedby="modal-modal-description"
      >
        <>
          <Box component="div" sx={style}>
            <Typography variant="inherit" color="primary">
              {`${
                countDown > 0
                  ? `Your session is expiring in ${countDown} seconds. `
                  : `Your session has expired at ${new Date().toISOString()}`
              }. If you want to extend current session, please click on "Continue
                session" button.`}
            </Typography>

            <Button
              style={{ alignSelf: "center" }}
              onClick={confirmHandler}
              variant="contained"
              sx={{ mt: 3, mb: 2 }}
            >
              Continue session
            </Button>
          </Box>
        </>
      </Modal>
    </>
  );
};
export default AddDeviceModal;
