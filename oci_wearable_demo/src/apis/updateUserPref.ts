import { axiosInstance } from "../utils";
import { UserPreferencesPayload } from "./addUserPref";
export const updateUserPref = ({
  username,
  userPreferences,
}: {
  username: string;
  userPreferences: UserPreferencesPayload;
}) =>
  axiosInstance.post(
    "/updateUserPref",
    { userPreferences },
    { params: { username } }
  );
