import { AxiosError, AxiosResponse } from 'axios';
import { QueryKey, useQuery, UseQueryOptions, UseQueryResult } from 'react-query';
import {
  QueryMapReservationsParamsV2,
  queryManagerMapReservationsV2,
} from 'api-v2/managerReservation';
import { ErrorResponse } from 'types/response';
import { QueryManagerMapReservationsSuccessV2 } from 'types/response-v2';

const useManagerMapReservationsV2 = <TData = AxiosResponse<QueryManagerMapReservationsSuccessV2>>(
  { mapId, date }: QueryMapReservationsParamsV2,
  options?: UseQueryOptions<
    AxiosResponse<QueryManagerMapReservationsSuccessV2>,
    AxiosError<ErrorResponse>,
    TData,
    [QueryKey, QueryMapReservationsParamsV2]
  >
): UseQueryResult<TData, AxiosError<ErrorResponse>> =>
  useQuery(['getManagerMapReservations', { mapId, date }], queryManagerMapReservationsV2, options);

export default useManagerMapReservationsV2;
