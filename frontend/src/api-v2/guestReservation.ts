import { AxiosResponse } from 'axios';
import apiV2 from './apiv2';

interface DeleteGuestReservationParamsV2 {
  mapId: number;
  spaceId: number;
  reservationId: number;
  password?: string;
}
export interface GuestReservationParamsV2 {
  reservation: {
    startDateTime: string;
    endDateTime: string;
    password: string;
    name: string;
    description: string;
  };
}

export interface PostGuestReservationParamsV2 extends GuestReservationParamsV2 {
  mapId: number;
  spaceId: number;
}

interface PutGuestReservationParamsV2 extends GuestReservationParamsV2 {
  mapId: number;
  spaceId: number;
  reservationId: number;
}

export const postGuestReservationV2 = ({
  reservation,
  mapId,
  spaceId,
}: PostGuestReservationParamsV2): Promise<AxiosResponse<never>> =>
  apiV2.post(`/api/maps/${mapId}/spaces/${spaceId}/reservations`, reservation);

export const putGuestReservationV2 = ({
  reservation,
  mapId,
  spaceId,
  reservationId,
}: PutGuestReservationParamsV2): Promise<AxiosResponse<never>> =>
  apiV2.put(`/api/maps/${mapId}/spaces/${spaceId}/reservations/${reservationId}`, reservation);

export const deleteGuestReservationV2 = ({
  mapId,
  spaceId,
  reservationId,
  password,
}: DeleteGuestReservationParamsV2): Promise<AxiosResponse<never>> =>
  apiV2.delete(`/api/maps/${mapId}/spaces/${spaceId}/reservations/${reservationId}`, {
    data: { password },
  });
