import React from "react";
import { axiosInstance } from "./../utils";
import { login } from "./../apis";
import { useMutation } from "@tanstack/react-query";
import SessionTimeOutModal from "./../components/SessionTimeOutModal";
import { baseURL, QueryParamsName } from "../constants";
import {
  AuthContext,
  HandleLoginFunctionType,
  InternalAuthState,
  JwtToken,
  parseJwt,
  TimeOutBuffer,
  useInjectToken,
} from "./useAuthUtils";

export const AuthProvider: React.FC<React.PropsWithChildren> = (props) => {
  const loginQuery = useMutation(login);

  // State that will be exposed externally
  const [authInfo, setAuthInfo] = React.useState<InternalAuthState>({
    authToken: null,
    userName: null,
    authTokenDetails: null,
    logInError: null,
    isModalOpen: false,
    password: null,
    setTimeoutTimer: null,
    authType: "appbased",
    apiGatewayToken: null,
  });

  // Show modal when JWT token is about to expire
  const setTimeoutTurnOnModal = (time: number) => {
    return setTimeout(
      () => setAuthInfo((state) => ({ ...state, isModalOpen: true })),
      time
    );
  };

  useInjectToken(authInfo, axiosInstance);

  // Handle API error: why we don't use onError handler? because the API returns
  // errorMessage in response with 200 status code. This applies to some other APIs, too
  React.useEffect(() => {
    setAuthInfo((authInfo) => {
      const errorMessage = loginQuery?.data?.data?.response?.errorMessage;
      return {
        ...authInfo,
        logInError: !!errorMessage
          ? loginQuery?.data?.data?.response
          : loginQuery?.isError
          ? loginQuery?.error
          : null,
      };
    });
  }, [
    loginQuery?.data?.data?.response,
    loginQuery?.isError,
    loginQuery?.error,
  ]);

  const handleLogin: HandleLoginFunctionType = (
    authType,
    { username, pass, callback }
  ) => {
    if (authType === "okta") {
      setAuthInfo({ ...authInfo, authType });
      const popup = window.open(
        `${baseURL}/login/okta`,
        "okta",
        "width=500,height=800"
      ) as Window;

      // message event listener for catching
      const messageEventListener: (ev: MessageEvent) => any = (
        ev: MessageEvent
      ) => {
        const data = ev?.data;
        if (!data) return;

        const params = new URLSearchParams(ev.data);
        const xApigwToken = data?.slice(
          data?.indexOf(`${QueryParamsName.xApigwToken}=`) +
            QueryParamsName.xApigwToken.length +
            1
        );

        const token = params.get("token");
        const authTokenDetails = parseJwt(token ?? "");
        if (!!xApigwToken && !!token) {
          setAuthInfo({
            ...authInfo,
            authTokenDetails,
            authToken: token,
            apiGatewayToken: xApigwToken,
            authType,
            userName: authTokenDetails?.sub,
          });
          popup.close();
          // clean up after logging in with okta
          window.removeEventListener("message", messageEventListener);
        }
      };
      window.addEventListener("message", messageEventListener);

      return;
    } else {
      loginQuery.mutate(
        {
          username,
          pass,
        },
        {
          onSuccess: (res) => {
            setAuthInfo(() => {
              const authTokenDetails: JwtToken = parseJwt(
                res?.data?.response?.token
              );
              return {
                authToken: res?.data?.response?.token,
                authTokenDetails,
                userName: username,
                logInError: null,
                isModalOpen: false,
                password: pass,
                setTimeoutTimer: !!authTokenDetails?.exp
                  ? setTimeoutTurnOnModal(
                      authTokenDetails.exp * 1000 - Date.now() - TimeOutBuffer
                    )
                  : null,
                apiGatewayToken: authInfo.apiGatewayToken,
                authType,
              };
            });
          },
        }
      );
    }
    if (!!callback && typeof callback === "function") {
      try {
        callback();
      } catch (e) {
        console.info("Below is error from login handler optional callback");
        console.warn(e);
      }
    }
  };
  const handleLogout = ({ callback }: { callback?: () => void }) => {
    setAuthInfo((authInfo) => {
      if (!!authInfo.setTimeoutTimer) {
        clearInterval(authInfo.setTimeoutTimer);
      }
      return {
        authToken: null,
        userName: null,
        authTokenDetails: null,
        logInError: null,
        isModalOpen: false,
        password: null,
        setTimeoutTimer: null,
        authType: "appbased",
        apiGatewayToken: null,
      };
    });
    if (!!callback) {
      try {
        callback();
      } catch (e) {
        console.warn(e);
      }
    }
  };

  return (
    <>
      {/* Session Timout Modal */}
      {authInfo.isModalOpen && (
        <SessionTimeOutModal
          confirmHandler={() => {
            if (!!authInfo?.authType) {
              handleLogin(authInfo?.authType, {
                username: authInfo.userName as string,
                pass: authInfo.password as string,
              });
            }
          }}
          //timeout in milliseconds
          expiration={TimeOutBuffer}
        />
      )}

      <AuthContext.Provider
        value={{
          logginError: authInfo.logInError,
          authTokenDetails: authInfo.authTokenDetails,
          isLoggingLoading: loginQuery.isLoading,
          authToken: authInfo.authToken,
          userName: authInfo.userName,
          handleLogin,
          handleLogout,
          authType: authInfo.authType,
          apiGatewayToken: authInfo.apiGatewayToken,
        }}
      >
        {props.children}
      </AuthContext.Provider>
    </>
  );
};

export const useAuthToken = () => React.useContext(AuthContext);
