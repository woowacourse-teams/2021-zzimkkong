import { AxiosError, AxiosResponse } from 'axios';
import { QueryKey, useQuery, UseQueryOptions, UseQueryResult } from 'react-query';
import { querySpaces, QuerySpacesParams } from 'api/space';
import { ErrorResponse, QuerySpacesSuccess } from 'types/response';

const useSpaces = <TData = AxiosResponse<QuerySpacesSuccess>>(
  { mapId, date }: QuerySpacesParams,
  options?: UseQueryOptions<
    AxiosResponse<QuerySpacesSuccess>,
    AxiosError<ErrorResponse>,
    TData,
    [QueryKey, QuerySpacesParams]
  >
): UseQueryResult<TData, AxiosError<ErrorResponse>> =>
  useQuery(['getSpaces', { mapId, date }], querySpaces, options);

export default useSpaces;
