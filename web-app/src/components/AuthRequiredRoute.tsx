import React from "react";
import { useAuthToken } from "./../hooks";
import { Navigate } from "react-router-dom";
import { AuthType } from "../models";

export const AuthRequiredRoute: React.FC<React.PropsWithChildren> = ({
  children,
}) => {
  const { authToken, authType, apiGatewayToken } = useAuthToken();
  const isUnauthorizedByAuthType: { [key in AuthType]: boolean } = {
    appbased: !authToken,
    okta: !authToken || !apiGatewayToken,
  };

  return isUnauthorizedByAuthType[authType] ? (
    <Navigate to={"/login"} />
  ) : (
    <>{children}</>
  );
};
