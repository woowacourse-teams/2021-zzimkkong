import { AxiosError, AxiosResponse } from 'axios';
import { QueryKey, useQuery, UseQueryOptions, UseQueryResult } from 'react-query';
import { queryGuestReservations, QuerySpaceReservationsParams } from 'api/guestReservation';
import { ErrorResponse, QueryGuestReservationsSuccess } from 'types/response';

const useGuestReservations = <TData = AxiosResponse<QueryGuestReservationsSuccess>>(
  { mapId, spaceId, date }: QuerySpaceReservationsParams,
  options?: UseQueryOptions<
    AxiosResponse<QueryGuestReservationsSuccess>,
    AxiosError<ErrorResponse>,
    TData,
    [QueryKey, QuerySpaceReservationsParams]
  >
): UseQueryResult<TData, AxiosError<ErrorResponse>> =>
  useQuery(['getReservations', { mapId, spaceId, date }], queryGuestReservations, options);

export default useGuestReservations;
