import { AxiosError, AxiosResponse } from 'axios';
import { QueryKey, useQuery, UseQueryOptions, UseQueryResult } from 'react-query';
import { QuerySpaceReservationsParamsV2, queryGuestReservationsV2 } from 'api-v2/guestReservation';
import { ErrorResponse } from 'types/response';
import { QueryGuestReservationsSuccessV2 } from 'types/response-v2';

const useGuestReservationsV2 = <TData = AxiosResponse<QueryGuestReservationsSuccessV2>>(
  { mapId, spaceId, date }: QuerySpaceReservationsParamsV2,
  options?: UseQueryOptions<
    AxiosResponse<QueryGuestReservationsSuccessV2>,
    AxiosError<ErrorResponse>,
    TData,
    [QueryKey, QuerySpaceReservationsParamsV2]
  >
): UseQueryResult<TData, AxiosError<ErrorResponse>> =>
  useQuery(['getGuestReservations', { mapId, spaceId, date }], queryGuestReservationsV2, options);

export default useGuestReservationsV2;
