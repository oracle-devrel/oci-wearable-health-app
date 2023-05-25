import * as React from "react";
import Button from "@mui/material/Button";
import { AuthType } from "../../models";

const buttonTextMapping: {
  [key in AuthType]: { text: string; loadingText: string };
} = {
  appbased: { text: "Sign in", loadingText: "Signing in" },
  okta: { text: "Okta sign in", loadingText: "Okta signing in" },
};

const SignInButton: React.FC<{
  assignedAuthType: AuthType;
  currentAuthType: AuthType;
  onClick: React.ComponentProps<typeof Button>["onClick"];
  isLoading: boolean;
}> = ({ assignedAuthType, currentAuthType, onClick, isLoading }) => {
  return (
    <Button
      onClick={onClick}
      fullWidth
      variant="contained"
      sx={{ mt: 3, mb: 2 }}
      disabled={isLoading}
    >
      {currentAuthType === assignedAuthType && isLoading
        ? buttonTextMapping[assignedAuthType].loadingText
        : buttonTextMapping[assignedAuthType].text}
    </Button>
  );
};
export { SignInButton };
