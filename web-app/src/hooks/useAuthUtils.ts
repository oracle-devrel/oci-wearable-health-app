import { AxiosInstance, AxiosRequestConfig } from "axios";
import * as React from "react";
import { AuthType } from "../models";

export type HandleLoginFunctionType = (
  authType: AuthType,
  {
    username,
    pass,
    callback,
  }: {
    username: string;
    pass: string;
    callback?: () => void;
  }
) => void;

type HandleLogoutFunctionType = ({
  callback,
}: {
  callback?: () => void;
}) => void;

export type LogInError = { errorCode: number; errorMessage: string } | null;

export interface JwtToken {
  sub: string;
  exp: number;
  scope: string;
}
// show time out modal 20 seconds (20000 milliseconds) before expiration
export const TimeOutBuffer: number = 20000 as const;
// Types: end

// State used internally for useAuth hook
export interface InternalAuthState {
  authToken: string | null;
  userName: string | null;
  authTokenDetails: JwtToken | null;
  logInError: LogInError;
  isModalOpen: boolean;
  password: string | null;
  setTimeoutTimer: NodeJS.Timeout | null;
  authType: AuthType;
  apiGatewayToken: string | null;
}

export interface AuthContextInterface {
  authToken: string | null;
  userName: string | null;
  logginError: LogInError;
  isLoggingLoading: boolean;
  authTokenDetails: JwtToken | null;
  handleLogin: HandleLoginFunctionType;
  handleLogout: HandleLogoutFunctionType;
  authType: AuthType;
  apiGatewayToken: string | null;
}
export const AuthContext = React.createContext<AuthContextInterface>({
  userName: null,
  authToken: null,
  isLoggingLoading: false,
  authTokenDetails: null,
  logginError: null,
  handleLogin: () => {},
  handleLogout: () => {},
  authType: "appbased",
  apiGatewayToken: null,
});

const parseJwt = (token: string) => {
  try {
    const base64Url = token?.split(".")[1];
    const base64 = base64Url?.replace(/-/g, "+").replace(/_/g, "/");
    const jsonPayload = decodeURIComponent(
      window
        .atob(base64 ?? "")
        .split("")
        .map(function (c) {
          return "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2);
        })
        .join("")
    );

    return JSON.parse(jsonPayload);
  } catch (e) {
    console.warn(e);
  }
};

/**
 * Dynamically inject authZ headers/query params based on Auth type
 * @param authInfo InternalAuthState
 * @param axiosInstance AxiosInstance
 */
const useInjectToken = (
  authInfo: InternalAuthState,
  axiosInstance: AxiosInstance
) => {
  const injectToken = React.useCallback(
    (config: AxiosRequestConfig): AxiosRequestConfig => {
      if (!config.headers) {
        config.headers = {};
      }

      if (!!authInfo?.authType) {
        config.headers["authType"] = authInfo?.authType;

        switch (authInfo.authType) {
          case "appbased": {
            config.headers["Authorization"] = `Bearer ${authInfo?.authToken}`;
            break;
          }
          case "okta": {
            config.params["token"] = authInfo.authToken;
            config.headers["X-APIGW-TOKEN"] = authInfo.apiGatewayToken;
            break;
          }
        }
      }

      return config;
    },
    [authInfo.authToken, authInfo.apiGatewayToken, authInfo.authType]
  );
  //when JWT token is updated, update the token in axios instance as well
  React.useEffect(() => {
    axiosInstance.interceptors.request.clear();
    axiosInstance.interceptors.request.use(injectToken);
  }, [
    authInfo.authToken,
    authInfo.apiGatewayToken,
    injectToken,
    authInfo.authType,
    axiosInstance.interceptors.request,
  ]);
};

export { parseJwt, useInjectToken };
