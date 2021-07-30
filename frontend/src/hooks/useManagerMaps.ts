import { AxiosError, AxiosResponse } from 'axios';
import { QueryKey, useQuery, UseQueryOptions, UseQueryResult } from 'react-query';
import { queryManagerMaps } from 'api/map';
import { QueryManagerMapsSuccess } from 'types/response';

const useManagerMaps = <TData = AxiosResponse<QueryManagerMapsSuccess>>(
  options?: UseQueryOptions<
    AxiosResponse<QueryManagerMapsSuccess>,
    AxiosError<Error>,
    TData,
    [QueryKey]
  >
): UseQueryResult<TData, AxiosError<Error>> =>
  useQuery(['getManagerMaps'], queryManagerMaps, options);

export default useManagerMaps;
