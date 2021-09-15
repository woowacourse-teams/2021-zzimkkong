import { AxiosError, AxiosResponse } from 'axios';
import { QueryKey, useQuery, UseQueryOptions, UseQueryResult } from 'react-query';
import { queryManagerMapReservations, QueryMapReservationsParams } from 'api/managerReservation';
import { ErrorResponse, QueryManagerMapReservationsSuccess } from 'types/response';

const useManagerMapReservations = <TData = AxiosResponse<QueryManagerMapReservationsSuccess>>(
  { mapId, date }: QueryMapReservationsParams,
  options?: UseQueryOptions<
    AxiosResponse<QueryManagerMapReservationsSuccess>,
    AxiosError<ErrorResponse>,
    TData,
    [QueryKey, QueryMapReservationsParams]
  >
): UseQueryResult<TData, AxiosError<ErrorResponse>> =>
  useQuery(['getManagerReservations', { mapId, date }], queryManagerMapReservations, options);

export default useManagerMapReservations;
