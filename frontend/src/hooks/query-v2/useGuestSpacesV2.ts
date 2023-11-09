import { AxiosError, AxiosResponse } from 'axios';
import { QueryKey, useQuery, UseQueryOptions, UseQueryResult } from 'react-query';
import { QueryGuestSpacesParamsV2, queryGuestSpacesV2 } from 'api-v2/guestSpace';
import { ErrorResponse } from 'types/response';
import { QuerySpacesSuccessV2 } from 'types/response-v2';

const useGuestSpacesV2 = <TData = AxiosResponse<QuerySpacesSuccessV2>>(
  { mapId }: QueryGuestSpacesParamsV2,
  options?: UseQueryOptions<
    AxiosResponse<QuerySpacesSuccessV2>,
    AxiosError<ErrorResponse>,
    TData,
    [QueryKey, QueryGuestSpacesParamsV2]
  >
): UseQueryResult<TData, AxiosError<ErrorResponse>> =>
  useQuery(['getGuestSpacesV2', { mapId }], queryGuestSpacesV2, options);

export default useGuestSpacesV2;
