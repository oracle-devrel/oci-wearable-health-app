import React from "react";
import { useQuery } from "@tanstack/react-query";
import { getUserPref } from "../../../apis";
import { useAuthToken } from "../../../hooks";
import PreferenceDisplayTable from "./PreferenceDisplayTable";
import { Typography } from "@mui/material";
import UserPreferenceForm from "./UserPreferenceForm";
import Spinner from "./../../../components/Spinner";

const getUserPrefQueryKey = "getUserPref";

export const UserPreferencePage: React.FC = () => {
  const { userName } = useAuthToken();

  const { data, isError, isLoading, error, refetch } = useQuery(
    [getUserPrefQueryKey],
    async () => {
      return await getUserPref({ username: userName as string });
    }
  );

  const preferenceFormSubmitSuccessCallback = React.useCallback(() => {
    refetch();
  }, [refetch]);
  const preferenceData = data?.data?.response;
  const isPreferenceExisting =
    typeof preferenceData !== "string" && typeof preferenceData === "object";

  if (isLoading) {
    return <Spinner />;
  }
  if (isError) {
    return <Typography>{`Error loading data! Details:${error}`}</Typography>;
  }

  return isPreferenceExisting ? (
    <>
      <PreferenceDisplayTable
        onUpdateSuccessCallback={refetch}
        userPreference={preferenceData}
      />
    </>
  ) : (
    <UserPreferenceForm
      onSuccessCallback={preferenceFormSubmitSuccessCallback}
    />
  );
};
