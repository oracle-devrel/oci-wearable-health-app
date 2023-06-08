import React from "react";
import ReactDOM from "react-dom/client";
import "./index.css";
import SignInSide from "./Pages/SignIn/SignIn";
import reportWebVitals from "./reportWebVitals";
import {
  createBrowserRouter,
  RouterProvider,
  Navigate,
} from "react-router-dom";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { AuthProvider } from "./hooks/useAuth";
import AdminDashBoard from "./Pages/AdminDashBoard/AdminDashboard";
import SignUp from "./Pages/SignUp/SignUp";
import { UserPreferencePage } from "./Pages/AdminDashBoard/UserPreferencePage/UserPreferencePage";
import { AuthRequiredRoute } from "./components/AuthRequiredRoute";
import DeviceManagement from "./Pages/AdminDashBoard/DeviceManagement/DeviceManagementPage";
import { ResourceNames } from "./constants";
// date picker adapters
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
const queryClient = new QueryClient();
const ProtectedAdminDashBoard = () => (
  <AuthRequiredRoute>
    <AdminDashBoard />
  </AuthRequiredRoute>
);
const router = createBrowserRouter([
  { path: ResourceNames.LoginPath, element: <SignInSide />, index: true },
  {
    path: ResourceNames.DashboardPath,
    element: <ProtectedAdminDashBoard />,
    children: [
      {
        index: true,
        path: ResourceNames.DashboardChildrenPaths.userPreference,
        element: <UserPreferencePage />,
      },
      {
        path: ResourceNames.DashboardChildrenPaths.deviceManagement,
        element: <DeviceManagement />,
      },
      { path: "*", element: <ProtectedAdminDashBoard /> },
    ],
    errorElement: <ProtectedAdminDashBoard />,
  },
  { path: ResourceNames.SignupPath, element: <SignUp /> },
  {
    path: "*",
    element: <Navigate to={ResourceNames.LoginPath} />,
    errorElement: <Navigate to={ResourceNames.LoginPath} />,
  },
]);

const root = ReactDOM.createRoot(
  document.getElementById("root") as HTMLElement
);
root.render(
  <React.StrictMode>
    <QueryClientProvider client={queryClient}>
      <LocalizationProvider dateAdapter={AdapterDayjs}>
        <AuthProvider>
          <RouterProvider router={router} />
        </AuthProvider>
      </LocalizationProvider>
    </QueryClientProvider>
  </React.StrictMode>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
