import { AxiosError, AxiosResponse } from 'axios';
import { QueryKey, useQuery, UseQueryOptions, UseQueryResult } from 'react-query';
import { queryManagerMapsV2 } from 'api-v2/managerMap';
import { ErrorResponse } from 'types/response';
import { QueryManagerMapsSuccessV2 } from 'types/response-v2';

const useManagerMapsV2 = <TData = AxiosResponse<QueryManagerMapsSuccessV2>>(
  options?: UseQueryOptions<
    AxiosResponse<QueryManagerMapsSuccessV2>,
    AxiosError<ErrorResponse>,
    TData,
    [QueryKey]
  >
): UseQueryResult<TData, AxiosError<ErrorResponse>> =>
  useQuery(['getManagerMapsV2'], queryManagerMapsV2, { ...options });

export default useManagerMapsV2;
