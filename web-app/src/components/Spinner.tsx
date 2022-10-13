import React from "react";
import CircularProgress from "@mui/material/CircularProgress";
import Grid from "@mui/material/Unstable_Grid2";
const Spinner: React.FC = () => {
  return (
    <Grid alignItems={"center"} xs={12} alignSelf={"center"} height={"80vh"}>
      <CircularProgress size={"5em"} />
    </Grid>
  );
};
export default Spinner;
