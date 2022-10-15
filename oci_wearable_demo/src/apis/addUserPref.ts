import { axiosInstance } from "../utils";
export interface UserPreferencesPayload {
  alertThresholdForHearRate: string;
  alertThresholdForBPHigh: string;
  alertThresholdForBPLow: string;
  alertThresholdForSPO2: string;
  alertThresholdForTemp: string;
  preferedAlertChannel: string;
  emergencyEmail: string;
  notificationFrequency: number;
  emergencyMobile: number;
}
const getOptionalParams = <T>(paramObj: T) => {
  return Object.keys(paramObj as any)?.reduce((pre, cur) => {
    return !!(paramObj as any)[cur]
      ? { ...pre, [cur]: (paramObj as any)[cur] }
      : pre;
  }, {} as Partial<T>);
};
export const addUserPref = ({
  username,
  userPreferences,
}: {
  username: string;
  userPreferences: UserPreferencesPayload;
}) => {
  return axiosInstance.post(
    "/addUserPref",
    {
      userPreferences: getOptionalParams(userPreferences),
    },
    { params: { username } }
  );
};
