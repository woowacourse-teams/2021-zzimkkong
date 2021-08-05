import { AxiosError, AxiosResponse } from 'axios';
import { QueryKey, useQuery, UseQueryOptions, UseQueryResult } from 'react-query';
import { queryGuestMap, QueryGuestMapParams } from 'api/guestMap';
import { ErrorResponse, QueryGuestMapSuccess } from 'types/response';

const useGuestMap = <TData = AxiosResponse<QueryGuestMapSuccess>>(
  publicMapId: string,
  options?: UseQueryOptions<
    AxiosResponse<QueryGuestMapSuccess>,
    AxiosError<ErrorResponse>,
    TData,
    [QueryKey, QueryGuestMapParams]
  >
): UseQueryResult<TData, AxiosError<ErrorResponse>> =>
  useQuery(['getGuestMap', { publicMapId }], queryGuestMap, options);

export default useGuestMap;
