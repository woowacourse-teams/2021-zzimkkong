import { AxiosError, AxiosResponse } from 'axios';
import { QueryKey, useQuery, UseQueryOptions, UseQueryResult } from 'react-query';
import { QueryGuestMapParamsV2, queryGuestMapV2 } from 'api-v2/guestMap';
import { ErrorResponse } from 'types/response';
import { QueryGuestMapSuccessV2 } from 'types/response-v2';

const useGuestMapV2 = <TData = AxiosResponse<QueryGuestMapSuccessV2>>(
  { sharingMapId }: QueryGuestMapParamsV2,
  options?: UseQueryOptions<
    AxiosResponse<QueryGuestMapSuccessV2>,
    AxiosError<ErrorResponse>,
    TData,
    [QueryKey, QueryGuestMapParamsV2]
  >
): UseQueryResult<TData, AxiosError<ErrorResponse>> =>
  useQuery(['getGuestMapV2', { sharingMapId }], queryGuestMapV2, options);

export default useGuestMapV2;
