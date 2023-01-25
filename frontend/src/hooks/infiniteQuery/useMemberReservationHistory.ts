import { useMemo } from 'react';
import { useInfiniteQuery } from 'react-query';
import { queryMemberReservationHistory } from './../../api/guestReservation';

const useMemberReservationHistory = () => {
  const infiniteQueryResponse = useInfiniteQuery(
    ['infiniteQueryMemberRreservationHistory'],
    ({ pageParam = 0 }) => queryMemberReservationHistory({ page: Number(pageParam) }),
    {
      getNextPageParam: (response) => response.data.hasNext,
    }
  );

  const flattedResults = useMemo(
    () => infiniteQueryResponse.data?.pages.flatMap((result) => result.data.data) ?? [],
    [infiniteQueryResponse.data]
  );

  return { ...infiniteQueryResponse, flattedResults };
};

export default useMemberReservationHistory;
