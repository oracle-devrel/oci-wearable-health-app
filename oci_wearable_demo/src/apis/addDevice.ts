import { axiosInstance } from "../utils";
import { Device } from "./getDevices";

export const addDevice = ({
  username,
  device,
}: {
  username: string;
  device: Device;
}) => axiosInstance.post("/addDevice", { device }, { params: { username } });
