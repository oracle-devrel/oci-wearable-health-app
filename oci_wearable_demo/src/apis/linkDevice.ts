import { axiosInstance } from "../utils";

export const linkDevice = ({
  username,
  serialNo,
}: {
  username: string;
  serialNo: string;
}) => axiosInstance.post("/link", null, { params: { username, serialNo } });
