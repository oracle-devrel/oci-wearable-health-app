import { axiosInstance } from "./../utils";

export interface NewUser {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  mobile: string;
  status: string;
}
export const newUserFieldNames: (keyof NewUser)[] = [
  "email",
  "firstName",
  "lastName",
  "mobile",
  "password",
];
export const getNewUserPayload = (user: Partial<NewUser>): Partial<NewUser> => {
  return Object.entries(user).reduce((pre, cur) => {
    return !!cur[1] ? { ...pre, [cur[0]]: cur[1] } : pre;
  }, {} as NewUser);
};
export const signup = (user: Partial<NewUser>) => {
  return axiosInstance.post("/addUser", {
    user: { ...getNewUserPayload(user), status: "active" },
  });
};
