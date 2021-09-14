import { AxiosError, AxiosResponse } from 'axios';
import { QueryKey, useQuery, UseQueryOptions, UseQueryResult } from 'react-query';
import { queryManagerSpaces, QueryManagerSpacesParams } from 'api/managerSpaces';
import { ErrorResponse, QueryManagerSpacesSuccess } from 'types/response';

const useManagerSpaces = <TData = AxiosResponse<QueryManagerSpacesSuccess>>(
  { mapId }: QueryManagerSpacesParams,
  options?: UseQueryOptions<
    AxiosResponse<QueryManagerSpacesSuccess>,
    AxiosError<ErrorResponse>,
    TData,
    [QueryKey, QueryManagerSpacesParams]
  >
): UseQueryResult<TData, AxiosError<ErrorResponse>> =>
  useQuery(['getManagerSpaces', { mapId }], queryManagerSpaces, options);

export default useManagerSpaces;
