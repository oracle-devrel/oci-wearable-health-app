import * as React from "react";
import ListItemButton from "@mui/material/ListItemButton";
import ListItemIcon from "@mui/material/ListItemIcon";
import ListItemText from "@mui/material/ListItemText";
import DevicesOtherIcon from "@mui/icons-material/DevicesOther";
import TuneIcon from "@mui/icons-material/Tune";
import { ResourceNames } from "./../../constants";
import { useNavigate, useMatch } from "react-router-dom";

const userPreferenceFullPath = `/${ResourceNames.DashboardPath}/${ResourceNames.DashboardChildrenPaths.userPreference}`;
const deviceManagementFullPath = `/${ResourceNames.DashboardPath}/${ResourceNames.DashboardChildrenPaths.deviceManagement}`;
export const MainListItems: React.FC = () => {
  const navigate = useNavigate();
  const isUserPreferenceActive = useMatch({
    path: userPreferenceFullPath,
    caseSensitive: false,
  });
  const isDeviceManagementActive = useMatch({
    path: deviceManagementFullPath,
    caseSensitive: false,
  });
  return (
    <>
      <ListItemButton
        sx={
          isUserPreferenceActive
            ? { backgroundColor: "rgba(0, 0, 0, 0.04)" }
            : null
        }
        onClick={() => {
          navigate(userPreferenceFullPath);
        }}
      >
        <ListItemIcon>
          <TuneIcon />
        </ListItemIcon>
        <ListItemText primary="User preferences" />
      </ListItemButton>
      <ListItemButton
        sx={
          isDeviceManagementActive
            ? { backgroundColor: "rgba(0, 0, 0, 0.04)" }
            : null
        }
        onClick={() => {
          navigate(deviceManagementFullPath);
        }}
      >
        <ListItemIcon>
          <DevicesOtherIcon />
        </ListItemIcon>
        <ListItemText primary="Device management" />
      </ListItemButton>
    </>
  );
};
