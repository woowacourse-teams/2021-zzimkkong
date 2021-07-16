import { AxiosError, AxiosResponse } from 'axios';
import { QueryKey, useQuery, UseQueryOptions, UseQueryResult } from 'react-query';
import { queryReservations, QueryReservationsParams } from 'api/reservation';
import { QueryReservationsSuccess } from 'types/response';

const useReservations = <TData = AxiosResponse<QueryReservationsSuccess>>(
  { mapId, spaceId, date }: QueryReservationsParams,
  options?: UseQueryOptions<
    AxiosResponse<QueryReservationsSuccess>,
    AxiosError<Error>,
    TData,
    [QueryKey, QueryReservationsParams]
  >
): UseQueryResult<TData, AxiosError<Error>> =>
  useQuery(['getReservations', { mapId, spaceId, date }], queryReservations, options);

export default useReservations;
