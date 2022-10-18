import * as React from "react";
import Avatar from "@mui/material/Avatar";
import Button from "@mui/material/Button";
import CssBaseline from "@mui/material/CssBaseline";
import TextField from "@mui/material/TextField";
import Container from "@mui/material/Container";
import Box from "@mui/material/Box";
import Grid from "@mui/material/Grid";
import LockOutlinedIcon from "@mui/icons-material/LockOutlined";
import Typography from "@mui/material/Typography";
import { createTheme, ThemeProvider } from "@mui/material/styles";
import { useAuthToken } from "../hooks";
import { useNavigate, Link as RouterLink } from "react-router-dom";
import Copyright from "./../components/Copyright";
import SnackBar from "./../components/CustomizedBottomCenterSnackbar";
import Carousel from "../components/Carousel";
import Card from "@mui/material/Card";
import CardContent from "@mui/material/CardContent";

const theme = createTheme();

export default function SignIn() {
  const { handleLogin, authToken, isLoggingLoading, logginError } =
    useAuthToken();
  const [loginSubmitted, setLoginSubmitted] = React.useState(false);
  const navigate = useNavigate();
  React.useEffect(() => {
    if (loginSubmitted && !!authToken) {
      navigate("/dashboard", { state: { loginSuccess: true } });
    }
  }, [authToken, navigate, loginSubmitted]);
  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const data = new FormData(event.currentTarget);
    handleLogin({
      username: data.get("email") as string,
      pass: data.get("password") as string,
      callback: () => {
        setLoginSubmitted(true);
      },
    });
  };

  return (
    <>
      <ThemeProvider theme={theme}>
        <Carousel />
        <Container component="main" sx={{zIndex:"1100"}} maxWidth="xs">
          <CssBaseline />

          <Card
            sx={{
              zIndex: "250",
              backgroundColor: "white",
              borderRadius: "4px",
            }}
          >
            <CardContent
              sx={{
                zIndex: "180",
                marginTop: 8,
                display: "flex",
                flexDirection: "column",
                alignItems: "center",
              }}
            >
              <Avatar sx={{ m: 1, bgcolor: "secondary.main" }}>
                <LockOutlinedIcon />
              </Avatar>
              <Typography zIndex="200" style={{fontSize:"2rem",fontWeight:"500"}} color="primary" component="h1" variant="h5">
                Sign in
              </Typography>
              <Box
                zIndex="200"
                component="form"
                onSubmit={handleSubmit}
                noValidate
                sx={{ mt: 1 }}
              >
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
                <Button
                  type="submit"
                  fullWidth
                  variant="contained"
                  sx={{ mt: 3, mb: 2 }}
                  disabled={isLoggingLoading}
                >
                  {isLoggingLoading ? "Signing in" : "Sign In"}
                </Button>
                <Grid container justifyContent={"center"}>
                  <Grid item>
                    <RouterLink to={"/signup"}>
                      {"Don't have an account? Sign Up"}
                    </RouterLink>
                  </Grid>
                </Grid>
              </Box>
            </CardContent>
          </Card>
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
