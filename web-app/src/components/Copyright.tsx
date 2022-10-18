import React from "react";
import Link from "@mui/material/Link";
import Typography from "@mui/material/Typography";
function Copyright(props: any) {
  return (
    <Typography
      variant="body2"
      color="text.secondary"
      align="center"
      style={{ position: "fixed", bottom: "1vh", left: "calc(50vw - 80px)" }}
      {...props}
    >
      {"Copyright © "}
      <Link color="inherit" href="https://www.oracle.com">
        Oracle
      </Link>{" "}
      {new Date().getFullYear()}
      {"."}
    </Typography>
  );
}

export default Copyright;
