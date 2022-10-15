import { axiosInstance } from "./../utils";

export const login = ({ username, pass }: { username: string; pass: string }) =>
  axiosInstance.post("/login", { username, pass });
