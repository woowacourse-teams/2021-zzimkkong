import { getManagerReservation, QueryMapReservationsParams } from 'api/reservation';
import { AxiosError, AxiosResponse } from 'axios';
import { QueryKey, useQuery, UseQueryOptions, UseQueryResult } from 'react-query';
import { QueryManagerReservationsSuccess } from 'types/response';

const useManagerReservations = <TData = AxiosResponse<QueryManagerReservationsSuccess>>(
  { mapId, date }: QueryMapReservationsParams,
  options?: UseQueryOptions<
    AxiosResponse<QueryManagerReservationsSuccess>,
    AxiosError<Error>,
    TData,
    [QueryKey, QueryMapReservationsParams]
  >
): UseQueryResult<TData, AxiosError<Error>> =>
  useQuery(['getManagerReservations', { mapId, date }], getManagerReservation, options);

export default useManagerReservations;
