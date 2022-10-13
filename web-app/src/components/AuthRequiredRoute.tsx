import React from "react";
import { useAuthToken } from "./../hooks";
import { Navigate } from "react-router-dom";
interface Props {
  children: any;
}
export const AuthRequiredRoute: React.FC<Props> = ({ children }) => {
  const { authToken } = useAuthToken();
  if (!authToken) return <Navigate to={"/login"} />;
  return <>{children}</>;
};
