import { AxiosResponse } from 'axios';
import { QueryFunction, QueryKey } from 'react-query';
import MESSAGE from 'constants/message';
import { QueryManagerReservationsSuccess } from 'types/response';
import api from './api';

export interface QueryMapReservationsParams {
  mapId: number | null;
  date: string;
}

export const queryManagerReservations: QueryFunction<
  AxiosResponse<QueryManagerReservationsSuccess>,
  [QueryKey, QueryMapReservationsParams]
> = ({ queryKey }) => {
  const [, data] = queryKey;
  const { mapId, date } = data;

  if (!mapId) {
    throw new Error(MESSAGE.RESERVATION.INVALID_MAP_ID);
  }

  return api.get(`/managers/maps/${mapId}/spaces/reservations?date=${date}`);
};
