import { useMemo } from 'react';
import { useInfiniteQuery } from 'react-query';
import { queryGuestNonLoginReservations } from '../../api/guestReservation';
import { isNullish } from '../../utils/type';

const useNonLoginReservations = (userName: string, searchStartTime: string) => {
  const infiniteQueryResponse = useInfiniteQuery(
    ['infiniteQueryNonLoginReservations'],
    ({ pageParam = 0 }) => {
      return queryGuestNonLoginReservations({
        userName: String(userName),
        searchStartTime: String(searchStartTime),
        page: Number(pageParam),
      });
    },
    {
      getNextPageParam: (response) => response.data.hasNext,
      refetchOnWindowFocus: false,
      refetchOnMount: false,
      refetchInterval: false,
      refetchOnReconnect: false,
    }
  );

  const flattedResults = useMemo(
    () => infiniteQueryResponse.data?.pages.flatMap((result) => result.data.data) ?? [],
    [infiniteQueryResponse.data]
  );

  return { ...infiniteQueryResponse, flattedResults };
};

export default useNonLoginReservations;
