import { AxiosError, AxiosResponse } from 'axios';
import { QueryKey, useQuery, UseQueryOptions, UseQueryResult } from 'react-query';
import { queryGuestSpaces, QueryGuestSpacesParams } from 'api/guestSpace';
import { ErrorResponse, QuerySpacesSuccess } from 'types/response';

const useGuestSpaces = <TData = AxiosResponse<QuerySpacesSuccess>>(
  { mapId }: QueryGuestSpacesParams,
  options?: UseQueryOptions<
    AxiosResponse<QuerySpacesSuccess>,
    AxiosError<ErrorResponse>,
    TData,
    [QueryKey, QueryGuestSpacesParams]
  >
): UseQueryResult<TData, AxiosError<ErrorResponse>> =>
  useQuery(['getGuestSpaces', { mapId }], queryGuestSpaces, options);

export default useGuestSpaces;
