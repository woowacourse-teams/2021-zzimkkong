import { AxiosError, AxiosResponse } from 'axios';
import { QueryKey, useQuery, UseQueryOptions, UseQueryResult } from 'react-query';
import {
  queryManagerSpaceReservations,
  QueryManagerSpaceReservationsParams,
} from 'api/managerReservation';
import { ErrorResponse, QueryManagerSpaceReservationsSuccess } from 'types/response';

const useManagerSpaceReservations = <TData = AxiosResponse<QueryManagerSpaceReservationsSuccess>>(
  { mapId, spaceId, date }: QueryManagerSpaceReservationsParams,
  options?: UseQueryOptions<
    AxiosResponse<QueryManagerSpaceReservationsSuccess>,
    AxiosError<ErrorResponse>,
    TData,
    [QueryKey, QueryManagerSpaceReservationsParams]
  >
): UseQueryResult<TData, AxiosError<ErrorResponse>> =>
  useQuery(
    ['getManagerSpaceReservations', { mapId, spaceId, date }],
    queryManagerSpaceReservations,
    options
  );

export default useManagerSpaceReservations;
