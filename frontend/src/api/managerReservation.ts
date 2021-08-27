import { AxiosResponse } from 'axios';
import { QueryFunction, QueryKey } from 'react-query';
import MESSAGE from 'constants/message';
import { QueryManagerReservationsSuccess } from 'types/response';
import api from './api';

export interface QueryMapReservationsParams {
  mapId: number | null;
  date: string;
}

interface ReservationParams {
  reservation: {
    startDateTime: Date;
    endDateTime: Date;
    name: string;
    description: string;
  };
}

interface PutReservationParams extends ReservationParams {
  mapId: number;
  spaceId: number;
  reservationId: number;
}

interface DeleteReservationParams {
  mapId: number;
  spaceId: number;
  reservationId: number;
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

export const putManagerReservation = ({
  reservation,
  mapId,
  spaceId,
  reservationId,
}: PutReservationParams): Promise<AxiosResponse<never>> =>
  api.put(`/managers/maps/${mapId}/spaces/${spaceId}/reservations/${reservationId}`, reservation);

export const deleteManagerReservation = ({
  mapId,
  spaceId,
  reservationId,
}: DeleteReservationParams): Promise<AxiosResponse<never>> =>
  api.delete(`/managers/maps/${mapId}/spaces/${spaceId}/reservations/${reservationId}`, {
    data: {},
  });
