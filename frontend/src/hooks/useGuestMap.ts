import { AxiosError, AxiosResponse } from 'axios';
import { QueryKey, useQuery, UseQueryOptions, UseQueryResult } from 'react-query';
import { ErrorResponse, QueryGuestMapSuccess } from 'types/response';
import { queryGuestMap, QueryGuestMapParams } from './../api/GuestMap';

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
