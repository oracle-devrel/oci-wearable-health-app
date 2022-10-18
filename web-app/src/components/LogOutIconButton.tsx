import React from "react";
import IconButton from "@mui/material/IconButton";
import Tooltip from "@mui/material/Tooltip";
import LogoutIcon from "@mui/icons-material/Logout";
import { useAuthToken } from "./../hooks";
export const LogOutIconButton: React.FC = () => {
  const { handleLogout } = useAuthToken();
  return (
    <Tooltip title="Log out">
      <IconButton
        onClick={() => {
          handleLogout({});
        }}
        color="inherit"
      >
        <LogoutIcon />
      </IconButton>
    </Tooltip>
  );
};
