import { AxiosError, AxiosResponse } from 'axios';
import { QueryKey, useQuery, UseQueryOptions, UseQueryResult } from 'react-query';
import { queryManagerMap, QueryManagerMapParams } from 'api/managerMap';
import { ErrorResponse, QueryManagerMapSuccess } from 'types/response';

const useManagerMap = <TData = AxiosResponse<QueryManagerMapSuccess>>(
  { mapId }: QueryManagerMapParams,
  options?: UseQueryOptions<
    AxiosResponse<QueryManagerMapSuccess>,
    AxiosError<ErrorResponse>,
    TData,
    [QueryKey, QueryManagerMapParams]
  >
): UseQueryResult<TData, AxiosError<ErrorResponse>> =>
  useQuery(['getMap', { mapId }], queryManagerMap, options);

export default useManagerMap;
