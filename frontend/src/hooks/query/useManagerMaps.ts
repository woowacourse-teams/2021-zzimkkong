import { AxiosError, AxiosResponse } from 'axios';
import { QueryKey, useQuery, UseQueryOptions, UseQueryResult } from 'react-query';
import { queryManagerMapsV2 } from 'api-v2/managerMap';
import { ErrorResponse } from 'types/response';
import { QueryManagerMapsSuccessV2 } from 'types/response-v2';

const useManagerMaps = <TData = AxiosResponse<QueryManagerMapsSuccessV2>>(
  options?: UseQueryOptions<
    AxiosResponse<QueryManagerMapsSuccessV2>,
    AxiosError<ErrorResponse>,
    TData,
    [QueryKey]
  >
): UseQueryResult<TData, AxiosError<ErrorResponse>> =>
  useQuery(['getManagerMaps'], queryManagerMapsV2, { ...options });

export default useManagerMaps;
