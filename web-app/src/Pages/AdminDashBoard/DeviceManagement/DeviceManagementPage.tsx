import React from "react";
import { useQuery } from "@tanstack/react-query";
import { getDevices } from "../../../apis";
import { useAuthToken, useClearQueryCache } from "../../../hooks";
import DeviceListTable from "./DeviceListTable";
import Spinner from "./../../../components/Spinner";
import Stack from "@mui/material/Stack";
import AddDeviceModal from "./AddDeviceModal";
import LinkDeviceModal from "./LinkDeviceModal";
const getDevicesQueryKey = "getDevices";
const DeviceManagementPage: React.FC = () => {
  const { userName } = useAuthToken();
  const { data, isLoading, refetch } = useQuery(
    [getDevicesQueryKey],
    async () => {
      return await getDevices({ username: userName as string });
    }
  );
  useClearQueryCache();

  return (
    <>
      <Stack alignSelf="center" spacing={2} direction="row">
        <AddDeviceModal onSubmitSuccessCallback={refetch} />
        <LinkDeviceModal onSubmitSuccessCallback={refetch} />
      </Stack>
      {isLoading ? (
        <Spinner />
      ) : (
        <DeviceListTable devices={data?.data?.response?.deviceList} />
      )}
    </>
  );
};
export default DeviceManagementPage;
