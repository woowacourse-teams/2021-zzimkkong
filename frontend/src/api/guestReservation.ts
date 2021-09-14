import { AxiosResponse } from 'axios';
import { QueryFunction, QueryKey } from 'react-query';
import MESSAGE from 'constants/message';
import { QueryGuestReservationsSuccess } from 'types/response';
import api from './api';

export interface QueryMapReservationsParams {
  mapId: number | null;
  date: string;
}

export interface QuerySpaceReservationsParams extends QueryMapReservationsParams {
  spaceId: number;
}

export interface ReservationParams {
  reservation: {
    startDateTime: Date;
    endDateTime: Date;
    password: string;
    name: string;
    description: string;
  };
}

interface PostReservationParams extends ReservationParams {
  mapId: number;
  spaceId: number;
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
  password: string;
}

export const queryGuestReservations: QueryFunction<
  AxiosResponse<QueryGuestReservationsSuccess>,
  [QueryKey, QuerySpaceReservationsParams]
> = ({ queryKey }) => {
  const [, data] = queryKey;
  const { mapId, spaceId, date } = data;

  if (!mapId) {
    throw new Error(MESSAGE.RESERVATION.INVALID_MAP_ID);
  }

  return api.get(`/guests/maps/${mapId}/spaces/${spaceId}/reservations?date=${date}`);
};

export const postGuestReservation = ({
  reservation,
  mapId,
  spaceId,
}: PostReservationParams): Promise<AxiosResponse<never>> =>
  api.post(`/guests/maps/${mapId}/spaces/${spaceId}/reservations`, reservation);

export const putGuestReservation = ({
  reservation,
  mapId,
  spaceId,
  reservationId,
}: PutReservationParams): Promise<AxiosResponse<never>> =>
  api.put(`/guests/maps/${mapId}/spaces/${spaceId}/reservations/${reservationId}`, reservation);

export const deleteGuestReservation = ({
  mapId,
  spaceId,
  reservationId,
  password,
}: DeleteReservationParams): Promise<AxiosResponse<never>> =>
  api.delete(`/guests/maps/${mapId}/spaces/${spaceId}/reservations/${reservationId}`, {
    data: { password },
  });
