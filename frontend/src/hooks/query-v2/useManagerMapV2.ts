import { AxiosError, AxiosResponse } from 'axios';
import { QueryKey, useQuery, UseQueryOptions, UseQueryResult } from 'react-query';
import { QueryManagerMapParamsV2, queryManagerMapV2 } from 'api-v2/managerMap';
import { QueryManagerMapParams } from 'api/managerMap';
import { ErrorResponse } from 'types/response';
import { QueryManagerMapSuccessV2 } from 'types/response-v2';

const useManagerMapV2 = <TData = AxiosResponse<QueryManagerMapSuccessV2>>(
  { mapId }: QueryManagerMapParams,
  options?: UseQueryOptions<
    AxiosResponse<QueryManagerMapSuccessV2>,
    AxiosError<ErrorResponse>,
    TData,
    [QueryKey, QueryManagerMapParamsV2]
  >
): UseQueryResult<TData, AxiosError<ErrorResponse>> =>
  useQuery(['getManagerMapV2', { mapId }], queryManagerMapV2, options);

export default useManagerMapV2;
