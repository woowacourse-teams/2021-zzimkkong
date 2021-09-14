import { AxiosError, AxiosResponse } from 'axios';
import { QueryKey, useQuery, UseQueryOptions, UseQueryResult } from 'react-query';
import { queryManagerSpace, QueryManagerSpaceParams } from 'api/managerSpace';
import { ErrorResponse, QueryManagerSpaceSuccess } from 'types/response';

const useManagerSpace = <TData = AxiosResponse<QueryManagerSpaceSuccess>>(
  { mapId, spaceId }: QueryManagerSpaceParams,
  options?: UseQueryOptions<
    AxiosResponse<QueryManagerSpaceSuccess>,
    AxiosError<ErrorResponse>,
    TData,
    [QueryKey, QueryManagerSpaceParams]
  >
): UseQueryResult<TData, AxiosError<ErrorResponse>> =>
  useQuery(['getManagerSpace', { mapId, spaceId }], queryManagerSpace, options);

export default useManagerSpace;
