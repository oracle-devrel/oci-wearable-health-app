import { axiosInstance } from "../utils";

export type Device = {
  deviceSerialNumber: string;
  deviceType: string;
  activationDate: string;
  status: "NEW" | "LINKED" | "IN-ACTIVE";
};
interface GetDevicesResponse {
  responseId: string;
  response: { deviceList: Device[] };
}
export const getDevices = ({ username }: { username: string }) =>
  axiosInstance.get<GetDevicesResponse>("/getDevice", { params: { username } });
