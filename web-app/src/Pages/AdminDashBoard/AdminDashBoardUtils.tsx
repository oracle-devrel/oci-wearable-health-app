import React from "react";
import { useMatch } from "react-router-dom";
import { ResourceNames } from "../../constants";
import { Typography, Grid } from "@mui/material";
import { useAuthToken } from "../../hooks";
import { Box } from "@mui/system";
import WatchIcon from "@mui/icons-material/Watch";
import { AuthType } from "../../models";
const PageTitle = ({ content }: { content: string }) => (
  <Typography
    align="center"
    component="h3"
    variant="h3"
    color="primary"
    marginBottom="1rem"
  >
    {content}
  </Typography>
);
/**
 * Get title display string for dashboard pages based on active route;
 */
export const usePageTitle = () => {
  const isDeviceManagementActive = useMatch(
    `/${ResourceNames.DashboardPath}/${ResourceNames.DashboardChildrenPaths.deviceManagement}`
  );
  const isUserPreferenceActive = useMatch(
    `/${ResourceNames.DashboardPath}/${ResourceNames.DashboardChildrenPaths.userPreference}`
  );
  let content: string = "";

  if (isDeviceManagementActive) {
    content = "Device management";
  } else if (isUserPreferenceActive) {
    content = "User preferences";
  }
  return <PageTitle content={content} />;
};

/**
 * return content for showing directions when user is at the /dashboard
 */
export const useDashBoardWelcomeContent = () => {
  const { userName } = useAuthToken();
  return useMatch(ResourceNames.DashboardPath) ? (
    <Box
      style={{
        height: "70vh",
        display: "grid",
        justifyContent: "center",
        alignItems: "center",
      }}
    >
      <Grid>
        <Typography align="center" component="article" variant="h5">
          {`Welcome, ${userName}!`}
        </Typography>
        <Typography align="center" component="article" variant="h5">
          {`Please choose any option on the left side navigation to start!
      `}
        </Typography>
      </Grid>
      <WatchIcon
        color="secondary"
        style={{ fontSize: "8rem", alignSelf: "center", justifySelf: "center" }}
      />
    </Box>
  ) : null;
};

export const loginSuccessSnackBarTextMapping: { [key in AuthType]: string } = {
  appbased: "Logged in successfully!",
  okta: "Logged in successfully with Okta!",
};
