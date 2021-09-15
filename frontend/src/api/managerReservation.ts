import { AxiosResponse } from 'axios';
import { QueryFunction, QueryKey } from 'react-query';
import THROW_ERROR from 'constants/throwError';
import {
  QueryManagerMapReservationsSuccess,
  QueryManagerSpaceReservationsSuccess,
} from 'types/response';
import api from './api';

export interface QueryMapReservationsParams {
  mapId: number | null;
  date: string;
}

export interface QueryManagerSpaceReservationsParams extends QueryMapReservationsParams {
  spaceId: number;
}

export interface ReservationParams {
  reservation: {
    startDateTime: Date;
    endDateTime: Date;
    name: string;
    description: string;
    password?: string;
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
}

export const queryManagerSpaceReservations: QueryFunction<
  AxiosResponse<QueryManagerSpaceReservationsSuccess>,
  [QueryKey, QueryManagerSpaceReservationsParams]
> = ({ queryKey }) => {
  const [, data] = queryKey;
  const { mapId, spaceId, date } = data;

  if (!mapId) {
    throw new Error(THROW_ERROR.INVALID_MAP_ID);
  }

  return api.get(`/managers/maps/${mapId}/spaces/${spaceId}/reservations?date=${date}`);
};

export const queryManagerMapReservations: QueryFunction<
  AxiosResponse<QueryManagerMapReservationsSuccess>,
  [QueryKey, QueryMapReservationsParams]
> = ({ queryKey }) => {
  const [, data] = queryKey;
  const { mapId, date } = data;

  if (!mapId) {
    throw new Error(THROW_ERROR.INVALID_MAP_ID);
  }

  return api.get(`/managers/maps/${mapId}/spaces/reservations?date=${date}`);
};

export const postManagerReservation = ({
  reservation,
  mapId,
  spaceId,
}: PostReservationParams): Promise<AxiosResponse<never>> =>
  api.post(`/managers/maps/${mapId}/spaces/${spaceId}/reservations`, reservation);

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
