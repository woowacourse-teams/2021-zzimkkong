import { QS } from '@toss/utils';
import { AxiosResponse } from 'axios';
import { QueryFunction, QueryKey } from 'react-query';
import { QueryManagerMapReservationsSuccessV2 } from 'types/response-v2';
import apiV2 from './apiv2';

export interface QueryMapReservationsParamsV2 {
  mapId: number;
  date: string;
}

export const queryManagerMapReservationsV2: QueryFunction<
  AxiosResponse<QueryManagerMapReservationsSuccessV2>,
  [QueryKey, QueryMapReservationsParamsV2]
> = ({ queryKey }) => {
  const [, data] = queryKey;
  const { mapId, date } = data;

  return apiV2.get(`/api/maps/${mapId}/spaces/reservations${QS.create({ date })}`);
};
