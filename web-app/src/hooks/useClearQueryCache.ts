import React from "react";
import { useQueryClient } from "@tanstack/react-query";

//When unmount, clear the query cache
export const useClearQueryCache = () => {
  const queryClient = useQueryClient();
  React.useEffect(() => {
    return () => queryClient.clear();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);
};
