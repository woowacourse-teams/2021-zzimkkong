import { AxiosError, AxiosResponse } from 'axios';
import { QueryKey, useQuery, UseQueryOptions, UseQueryResult } from 'react-query';
import { QueryGuestSpaceParams, queryGusetSpace } from 'api/guestSpace';
import { ErrorResponse, QueryGuestSpaceSuccess } from 'types/response';

const useGuestSpace = <TData = AxiosResponse<QueryGuestSpaceSuccess>>(
  { mapId, spaceId }: QueryGuestSpaceParams,
  options?: UseQueryOptions<
    AxiosResponse<QueryGuestSpaceSuccess>,
    AxiosError<ErrorResponse>,
    TData,
    [QueryKey, QueryGuestSpaceParams]
  >
): UseQueryResult<TData, AxiosError<ErrorResponse>> =>
  useQuery(['getGuestSpace', { mapId, spaceId }], queryGusetSpace, options);

export default useGuestSpace;
