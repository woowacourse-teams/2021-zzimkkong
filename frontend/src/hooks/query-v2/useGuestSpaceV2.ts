import { AxiosError, AxiosResponse } from 'axios';
import { QueryKey, useQuery, UseQueryOptions, UseQueryResult } from 'react-query';
import { QueryGuestSpaceParamsV2, queryGuestSpaceV2 } from 'api-v2/guestSpace';
import { ErrorResponse } from 'types/response';
import { QueryGuestSpaceSuccessV2 } from 'types/response-v2';

const useGuestSpaceV2 = <TData = AxiosResponse<QueryGuestSpaceSuccessV2>>(
  { mapId, spaceId }: QueryGuestSpaceParamsV2,
  options?: UseQueryOptions<
    AxiosResponse<QueryGuestSpaceSuccessV2>,
    AxiosError<ErrorResponse>,
    TData,
    [QueryKey, QueryGuestSpaceParamsV2]
  >
): UseQueryResult<TData, AxiosError<ErrorResponse>> =>
  useQuery(['getGuestSpaceV2', { mapId, spaceId }], queryGuestSpaceV2, options);

export default useGuestSpaceV2;
