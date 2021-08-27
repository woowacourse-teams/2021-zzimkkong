import { AxiosError, AxiosResponse } from 'axios';
import { QueryKey, useQuery, UseQueryOptions, UseQueryResult } from 'react-query';
import { queryManagerMaps } from 'api/managerMap';
import { ErrorResponse, QueryManagerMapsSuccess } from 'types/response';

const useManagerMaps = <TData = AxiosResponse<QueryManagerMapsSuccess>>(
  options?: UseQueryOptions<
    AxiosResponse<QueryManagerMapsSuccess>,
    AxiosError<ErrorResponse>,
    TData,
    [QueryKey]
  >
): UseQueryResult<TData, AxiosError<ErrorResponse>> =>
  useQuery(['getManagerMaps'], queryManagerMaps, options);

export default useManagerMaps;
