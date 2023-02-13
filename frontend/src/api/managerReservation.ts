import { QS } from '@toss/utils';
import { AxiosResponse } from 'axios';
import { QueryFunction, QueryKey } from 'react-query';
import {
  QueryManagerMapReservationsSuccess,
  QueryManagerSpaceReservationsSuccess,
} from 'types/response';
import api from './api';

export interface QueryMapReservationsParams {
  mapId: number;
  date: string;
}

export interface QueryManagerSpaceReservationsParams extends QueryMapReservationsParams {
  spaceId: number;
}

export interface PostReservationParams {
  mapId: number;
  spaceId: number;
  reservation: {
    startDateTime: string;
    endDateTime: string;
    name: string;
    description: string;
    password: string;
  };
}

export interface PutReservationParams {
  mapId: number;
  spaceId: number;
  reservationId: number;
  reservation: {
    startDateTime: string;
    endDateTime: string;
    name: string;
    description: string;
  };
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

  return api.get(`/managers/maps/${mapId}/spaces/${spaceId}/reservations${QS.create({ date })}`);
};

export const queryManagerMapReservations: QueryFunction<
  AxiosResponse<QueryManagerMapReservationsSuccess>,
  [QueryKey, QueryMapReservationsParams]
> = ({ queryKey }) => {
  const [, data] = queryKey;
  const { mapId, date } = data;

  return api.get(`/managers/maps/${mapId}/spaces/reservations${QS.create({ date })}`);
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
