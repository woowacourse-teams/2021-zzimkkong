import { AxiosError, AxiosResponse } from 'axios';
import { QueryKey, useQuery, UseQueryOptions, UseQueryResult } from 'react-query';
import { queryManagerReservations, QueryMapReservationsParams } from 'api/managerReservation';
import { ErrorResponse, QueryManagerReservationsSuccess } from 'types/response';

const useManagerReservations = <TData = AxiosResponse<QueryManagerReservationsSuccess>>(
  { mapId, date }: QueryMapReservationsParams,
  options?: UseQueryOptions<
    AxiosResponse<QueryManagerReservationsSuccess>,
    AxiosError<ErrorResponse>,
    TData,
    [QueryKey, QueryMapReservationsParams]
  >
): UseQueryResult<TData, AxiosError<ErrorResponse>> =>
  useQuery(['getManagerReservations', { mapId, date }], queryManagerReservations, options);

export default useManagerReservations;
