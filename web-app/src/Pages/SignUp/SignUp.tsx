import * as React from "react";
import Avatar from "@mui/material/Avatar";
import Button from "@mui/material/Button";
import CssBaseline from "@mui/material/CssBaseline";
import TextField from "@mui/material/TextField";
import Grid from "@mui/material/Grid";
import Box from "@mui/material/Box";
import LockOutlinedIcon from "@mui/icons-material/LockOutlined";
import Typography from "@mui/material/Typography";
import Container from "@mui/material/Container";
import { createTheme, ThemeProvider } from "@mui/material/styles";
import { Link as RouterLink, useNavigate } from "react-router-dom";
import { signup, newUserFieldNames, NewUser } from "../../apis";
import { useMutation } from "@tanstack/react-query";
import Copyright from "../../components/Copyright";
import SnackBar from "../../components/CustomizedBottomCenterSnackbar";
import { ResourceNames } from "../../constants";

const theme = createTheme();

export const SignUp: React.FC = () => {
  const navigate = useNavigate();

  const { mutate, isLoading, isSuccess, data, isError, error } = useMutation(
    signup,
    {
      onSuccess: () => {
        setTimeout(() => {
          navigate(`/${ResourceNames.LoginPath}`, {
            state: { signupState: "successful" },
          });
        }, 5000);
      },
    }
  );
  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const data = new FormData(event.currentTarget);
    mutate(
      newUserFieldNames.reduce((pre, cur) => {
        const currentFieldVal = data.get(cur);
        return !!currentFieldVal ? { ...pre, [cur]: currentFieldVal } : pre;
      }, {} as Partial<NewUser>)
    );
  };
  return (
    <>
      <ThemeProvider theme={theme}>
        <Container component="main" maxWidth="xs">
          <CssBaseline />
          <Box
            sx={{
              display: "flex",
              flexDirection: "column",
              alignItems: "center",
              marginTop: "calc(50vh - 300px)",
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
              Sign up
            </Typography>
            <Box
              component="form"
              noValidate
              onSubmit={handleSubmit}
              sx={{ mt: 3 }}
            >
              <Grid container spacing={2}>
                <Grid item xs={12} sm={6}>
                  <TextField
                    name="firstName"
                    fullWidth
                    id="firstName"
                    label="First Name"
                    autoFocus
                  />
                </Grid>
                <Grid item xs={12} sm={6}>
                  <TextField
                    fullWidth
                    id="lastName"
                    label="Last Name"
                    name="lastName"
                  />
                </Grid>
                <Grid item xs={12}>
                  <TextField
                    fullWidth
                    id="phoneNumber"
                    label="Mobile"
                    name="phoneNumber"
                    autoComplete="phone-number"
                  />
                </Grid>
                <Grid item xs={12}>
                  <TextField
                    required
                    fullWidth
                    id="email"
                    label="Email Address"
                    name="email"
                    autoComplete="email"
                  />
                </Grid>
                <Grid item xs={12}>
                  <TextField
                    required
                    fullWidth
                    name="password"
                    label="Password"
                    type="password"
                    id="password"
                    autoComplete="new-password"
                  />
                </Grid>
              </Grid>
              <Button
                disabled={isLoading || isSuccess}
                type="submit"
                fullWidth
                variant="contained"
                sx={{ mt: 3, mb: 2 }}
              >
                {isLoading ? "Processing data" : "Sign Up"}
              </Button>
              <Grid container justifyContent="flex-end">
                <Grid item>
                  <RouterLink to={"/login"}>
                    Already have an account? Sign in
                  </RouterLink>
                </Grid>
              </Grid>
            </Box>
          </Box>
          <Copyright sx={{ mt: 5 }} />
        </Container>
      </ThemeProvider>
      {isError && !!error && (
        <SnackBar severity="error" msg={error as string} />
      )}
      {!!data?.data?.response?.errorMessage && (
        <SnackBar severity="error" msg={data?.data?.response?.errorMessage} />
      )}
      {isSuccess && !data?.data?.response?.errorMessage && (
        <SnackBar
          severity="success"
          msg="Signed up successfully! Will automatically redirect you to log in page in 5s.."
        />
      )}
    </>
  );
};
export default SignUp;
