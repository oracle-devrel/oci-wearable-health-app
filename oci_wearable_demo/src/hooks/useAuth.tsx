import React from "react";
import { axiosInstance } from "./../utils";
import { AxiosRequestConfig } from "axios";
import { login } from "./../apis";
import { useMutation } from "@tanstack/react-query";
import SessionTimeOutModal from "./../components/SessionTimeOutModal";
// types:begin
type HandleLoginFunctionType = ({
  username,
  pass,
  callback,
}: {
  username: string;
  pass: string;
  callback?: () => void;
}) => void;

type HandleLogoutFunctionType = ({
  callback,
}: {
  callback?: () => void;
}) => void;
type LogInError = { errorCode: number; errorMessage: string } | null;
interface JwtToken {
  sub: string;
  exp: number;
  scope: string;
}
// show time out modal 20 seconds (20000 milliseconds) before expiration
const TimeOutBuffer: number = 20000 as const;
// Types: end

export const AuthContext = React.createContext<{
  authToken: string | null;
  userName: string | null;
  logginError: LogInError;
  isLoggingLoading: boolean;
  authTokenDetails: JwtToken | null;
  handleLogin: HandleLoginFunctionType;
  handleLogout: HandleLogoutFunctionType;
}>({
  userName: null,
  authToken: null,
  isLoggingLoading: false,
  authTokenDetails: null,
  logginError: null,
  handleLogin: () => {},
  handleLogout: () => {},
});

export const parseJwt = (token: string) => {
  try {
    var base64Url = token.split(".")[1];
    var base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
    var jsonPayload = decodeURIComponent(
      window
        .atob(base64)
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
type Props = { children: any };

export const AuthProvider: React.FC<Props> = (props: Props) => {
  const { mutate, isLoading, data, error, isError } = useMutation(login);

  // State that will be exposed externally
  const [authInfo, setAuthInfo] = React.useState<{
    authToken: string | null;
    userName: string | null;
    authTokenDetails: JwtToken | null;
    logInError: LogInError;
    isModalOpen: boolean;
    password: string | null;
    setTimeoutTimer: NodeJS.Timeout | null;
  }>({
    authToken: null,
    userName: null,
    authTokenDetails: null,
    logInError: null,
    isModalOpen: false,
    password: null,
    setTimeoutTimer: null,
  });

  // Show modal when JWT token is about to expire
  const setTimeoutTurnOnModal = (time: number) => {
    return setTimeout(
      () => setAuthInfo((state) => ({ ...state, isModalOpen: true })),
      time
    );
  };

  const injectToken = React.useCallback(
    (config: AxiosRequestConfig): AxiosRequestConfig => {
      if (!config.headers) {
        config.headers = {};
      }
      config.headers["Authorization"] = `Bearer ${authInfo.authToken}`;
      return config;
    },
    [authInfo.authToken]
  );
  //when JWT token is updated, update the token in axios instance as well
  React.useEffect(() => {
    if (!!authInfo.authToken) {
      axiosInstance.interceptors.request.clear();
      axiosInstance.interceptors.request.use(injectToken);
    }
  }, [authInfo.authToken, injectToken]);

  // Handle API error: why we don't use onError handler? because the API returns
  // errorMessage in response with 200 status code. This applies to some other APIs, too
  React.useEffect(() => {
    setAuthInfo((authInfo) => {
      const errorMessage = data?.data?.response?.errorMessage;
      return {
        ...authInfo,
        logInError: !!errorMessage
          ? data?.data?.response
          : isError
          ? error
          : null,
      };
    });
  }, [data?.data?.response, isError, error]);

  const handleLogin: HandleLoginFunctionType = ({
    username,
    pass,
    callback,
  }) => {
    mutate(
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
            };
          });
        },
      }
    );
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
      {authInfo.isModalOpen && (
        <SessionTimeOutModal
          confirmHandler={() => {
            handleLogin({
              username: authInfo.userName as string,
              pass: authInfo.password as string,
            });
          }}
          //timeout in milliseconds
          expiration={TimeOutBuffer}
        />
      )}
      <AuthContext.Provider
        value={{
          logginError: authInfo.logInError,
          authTokenDetails: authInfo.authTokenDetails,
          isLoggingLoading: isLoading,
          authToken: authInfo.authToken,
          userName: authInfo.userName,
          handleLogin,
          handleLogout,
        }}
      >
        {props.children}
      </AuthContext.Provider>
    </>
  );
};

export const useAuthToken = () => React.useContext(AuthContext);
