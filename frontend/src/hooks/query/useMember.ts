import { AxiosError, AxiosResponse } from 'axios';
import { QueryKey, useQuery, UseQueryOptions, UseQueryResult } from 'react-query';
import { queryMember, QueryMemberSuccess } from 'api/member';

const useMember = <TData = AxiosResponse<QueryMemberSuccess>>(
  options?: UseQueryOptions<AxiosResponse<QueryMemberSuccess>, AxiosError, TData, [QueryKey]>
): UseQueryResult<TData, AxiosError> =>
  useQuery(['getMember'], queryMember, {
    ...options,
    refetchOnWindowFocus: false,
    cacheTime: 300_000,
  });

export default useMember;
