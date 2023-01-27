import { useMemo } from 'react';
import { useInfiniteQuery } from 'react-query';
import { queryMemberReservations } from '../../api/guestReservation';

const useMemberReservations = () => {
  const infiniteQueryResponse = useInfiniteQuery(
    ['infiniteQueryMemberRreservations'],
    ({ pageParam = 0 }) => queryMemberReservations({ page: Number(pageParam) }),
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

export default useMemberReservations;
