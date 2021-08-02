import { AxiosError, AxiosResponse } from 'axios';
import { QueryKey, useQuery, UseQueryOptions, UseQueryResult } from 'react-query';
import { queryGuestReservations, QuerySpaceReservationsParams } from 'api/reservation';
import { ErrorResponse, QueryReservationsSuccess } from 'types/response';

const useReservations = <TData = AxiosResponse<QueryReservationsSuccess>>(
  { mapId, spaceId, date }: QuerySpaceReservationsParams,
  options?: UseQueryOptions<
    AxiosResponse<QueryReservationsSuccess>,
    AxiosError<ErrorResponse>,
    TData,
    [QueryKey, QuerySpaceReservationsParams]
  >
): UseQueryResult<TData, AxiosError<ErrorResponse>> =>
  useQuery(['getReservations', { mapId, spaceId, date }], queryGuestReservations, options);

export default useReservations;
