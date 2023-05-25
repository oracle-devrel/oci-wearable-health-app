import { UserPreferencesPayload } from "./apis";
export enum EnvironmentVariableNames {
  REACT_APP_API_GATEWAY_ENDPOINT = "REACT_APP_API_GATEWAY_ENDPOINT",
}
export namespace ResourceNames {
  export const LoginPath = "login";
  export const DashboardPath = "dashboard";
  export const DashboardChildrenPaths = {
    userPreference: "user-preference",
    deviceManagement: "device-management",
  };
  export const SignupPath = "signup";
}
// In production, the api endpoint should be read from environment variable REACT_APP_API_GATEWAY_ENDPOINT
const endpointFromEnv =
  process.env[EnvironmentVariableNames.REACT_APP_API_GATEWAY_ENDPOINT];
// export const baseURL = `https://${endpointFromEnv}/admin-api`;
export const baseURL = `https://${endpointFromEnv}/openid-api`;
// export const openIdbaseURL = `https://${endpointFromEnv}/openid-api`;
// Mapping from create user preference payload fields name ==> to actual rendered string in UI
export const diplayStringMap: Record<keyof UserPreferencesPayload, string> = {
  alertThresholdForBPHigh: "Alert threshold for high BP",
  alertThresholdForBPLow: "Alert threshold for low BP",
  alertThresholdForHearRate: "Alert threshold for heart rate",
  alertThresholdForSPO2: "Alert threshold for SPO2",
  alertThresholdForTemp: "Alert threshold for Temperature",
  emergencyEmail: "Emergency email",
  notificationFrequency: "Notification Frequency",
  preferedAlertChannel: "Preferred alert channel",
  emergencyMobile: "Emergency mobile phone number",
};

export namespace QueryParamsName {
  export const xApigwToken = "xApigwToken";
}
