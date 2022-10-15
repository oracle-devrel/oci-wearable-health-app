import { axiosInstance } from "../utils";
export const getUserPref = ({ username }: { username: string }) =>
  axiosInstance.get("/getUserPref", { params: { username } });
