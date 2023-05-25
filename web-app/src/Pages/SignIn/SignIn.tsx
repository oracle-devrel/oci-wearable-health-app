import * as React from "react";
import Avatar from "@mui/material/Avatar";
import CssBaseline from "@mui/material/CssBaseline";
import TextField from "@mui/material/TextField";
import Container from "@mui/material/Container";
import Box from "@mui/material/Box";
import Grid from "@mui/material/Grid";
import LockOutlinedIcon from "@mui/icons-material/LockOutlined";
import Typography from "@mui/material/Typography";
import { createTheme, ThemeProvider } from "@mui/material/styles";
import { useAuthToken } from "../../hooks";
import { useNavigate, Link as RouterLink } from "react-router-dom";
import Copyright from "../../components/Copyright";
import SnackBar from "../../components/CustomizedBottomCenterSnackbar";
import { AuthType } from "../../models";
import { SignInButton } from "./SignInButton";
const theme = createTheme();
export default function SignIn() {
  const {
    handleLogin,
    authToken,
    isLoggingLoading,
    logginError,
    apiGatewayToken,
    authType,
  } = useAuthToken();
  const [loginSubmitted, setLoginSubmitted] = React.useState(false);
  const navigate = useNavigate();
  /**
   * Navigate to dashboard page when authorized.
   */
  React.useEffect(() => {
    const isAuthorizedByAuthType: { [key in AuthType]: boolean } = {
      okta: !!authToken && !!apiGatewayToken,
      appbased: loginSubmitted && !!authToken,
    };
    isAuthorizedByAuthType[authType] &&
      navigate("/dashboard", { state: { loginSuccess: true } });
  }, [authToken, navigate, loginSubmitted, apiGatewayToken, authType]);
  const handleSubmit = (authType: AuthType) => {
    const data = new FormData(logInFormRef?.current);
    handleLogin(authType, {
      username: data.get("email") as string,
      pass: data.get("password") as string,
      callback: () => {
        setLoginSubmitted(true);
      },
    });
  };

  const logInFormRef = React.useRef<HTMLFormElement>();
  return (
    <>
      <ThemeProvider theme={theme}>
        <Container
          component="main"
          maxWidth="xs"
          style={{
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            marginTop: "calc(50vh - 300px)",
          }}
        >
          <CssBaseline />
          <Box
            sx={{
              marginTop: 8,
              display: "flex",
              flexDirection: "column",
              alignItems: "center",
            }}
          >
            <Avatar sx={{ m: 1, bgcolor: "secondary.main" }}>
              <LockOutlinedIcon />
            </Avatar>
            <Typography
              style={{ fontSize: "2rem", fontWeight: "500" }}
              color="primary"
              component="h1"
              variant="h5"
            >
              Sign in
            </Typography>
            <Box component="form" noValidate sx={{ mt: 1 }} ref={logInFormRef}>
              <TextField
                margin="normal"
                required
                fullWidth
                id="email"
                label="Email Address"
                name="email"
                autoComplete="email"
                autoFocus
              />
              <TextField
                margin="normal"
                required
                fullWidth
                name="password"
                label="Password"
                type="password"
                id="password"
                autoComplete="current-password"
              />
              <SignInButton
                onClick={() => {
                  handleSubmit("appbased");
                }}
                assignedAuthType={"appbased"}
                currentAuthType={authType}
                isLoading={isLoggingLoading}
              />

              <Grid container justifyContent={"center"}>
                <Grid item>
                  <RouterLink to={"/signup"}>
                    {"Don't have an account? Sign Up"}
                  </RouterLink>
                </Grid>
              </Grid>
              <SignInButton
                onClick={() => {
                  handleSubmit("okta");
                }}
                assignedAuthType={"okta"}
                currentAuthType={authType}
                isLoading={isLoggingLoading}
              />
            </Box>
          </Box>
          <Copyright sx={{ mt: 8, mb: 4 }} />
        </Container>
      </ThemeProvider>
      {!!logginError && (
        <SnackBar
          msg={logginError?.errorMessage ?? "Login Error!"}
          severity="error"
        />
      )}
    </>
  );
}
