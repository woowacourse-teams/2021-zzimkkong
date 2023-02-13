import { AxiosResponse } from 'axios';
import { QueryFunction, QueryKey } from 'react-query';
import THROW_ERROR from 'constants/throwError';
import { QueryGuestReservationsSuccess, QueryMemberReservationsSuccess } from 'types/response';
import api from './api';

export interface QueryMapReservationsParams {
  mapId: number;
  date: string;
}

export interface QuerySpaceReservationsParams extends QueryMapReservationsParams {
  spaceId: number;
}

export interface QueryNonLoginReservationsParams {
  userName: string;
  searchStartTime: string;
  page: number;
}

export interface QueryMemberReservationsParams {
  page: number;
}

export interface GuestReservationParams {
  reservation: {
    startDateTime: string;
    endDateTime: string;
    password: string;
    name: string;
    description: string;
  };
}

export interface PostGuestReservationParams extends GuestReservationParams {
  mapId: number;
  spaceId: number;
}

interface PutGuestReservationParams extends GuestReservationParams {
  mapId: number;
  spaceId: number;
  reservationId: number;
}

interface DeleteGuestReservationParams {
  mapId: number;
  spaceId: number;
  reservationId: number;
  password?: string;
}

export interface MemberGuestReservationParams {
  reservation: {
    startDateTime: string;
    endDateTime: string;
    description: string;
  };
}

export interface PostMemberGuestReservationParams extends MemberGuestReservationParams {
  mapId: number;
  spaceId: number;
}

interface PutMemberGuestReservationParams extends MemberGuestReservationParams {
  mapId: number;
  spaceId: number;
  reservationId: number;
}

export const queryGuestReservations: QueryFunction<
  AxiosResponse<QueryGuestReservationsSuccess>,
  [QueryKey, QuerySpaceReservationsParams]
> = ({ queryKey }) => {
  const [, data] = queryKey;
  const { mapId, spaceId, date } = data;

  if (!mapId) {
    throw new Error(THROW_ERROR.INVALID_MAP_ID);
  }

  return api.get(`/guests/maps/${mapId}/spaces/${spaceId}/reservations?date=${date}`);
};

// 비회원 예약 조회
export const queryGuestNonLoginReservations = ({
  userName,
  searchStartTime,
  page,
}: QueryNonLoginReservationsParams): Promise<AxiosResponse<QueryMemberReservationsSuccess>> => {
  return api.get(
    `/guests/non-login/reservations?userName=${userName}&searchStartTime=${searchStartTime}&page=${page}`
  );
};

// 내 예약 조회
export const queryMemberReservations = ({
  page,
}: QueryMemberReservationsParams): Promise<AxiosResponse<QueryMemberReservationsSuccess>> => {
  return api.get(`/guests/reservations?page=${page}`);
};

// 내 이전 예약 조회
export const queryMemberReservationHistory = ({
  page,
}: QueryMemberReservationsParams): Promise<AxiosResponse<QueryMemberReservationsSuccess>> => {
  return api.get(`/guests/reservations/history?page=${page}`);
};

export const postGuestReservation = ({
  reservation,
  mapId,
  spaceId,
}: PostGuestReservationParams): Promise<AxiosResponse<never>> =>
  api.post(`/guests/maps/${mapId}/spaces/${spaceId}/reservations`, reservation);

export const putGuestReservation = ({
  reservation,
  mapId,
  spaceId,
  reservationId,
}: PutGuestReservationParams): Promise<AxiosResponse<never>> =>
  api.put(`/guests/maps/${mapId}/spaces/${spaceId}/reservations/${reservationId}`, reservation);

export const deleteGuestReservation = ({
  mapId,
  spaceId,
  reservationId,
  password,
}: DeleteGuestReservationParams): Promise<AxiosResponse<never>> =>
  api.delete(`/guests/maps/${mapId}/spaces/${spaceId}/reservations/${reservationId}`, {
    data: { password },
  });

export const deleteMemberReservation = ({
  mapId,
  spaceId,
  reservationId,
}: Omit<DeleteGuestReservationParams, 'password'>): Promise<AxiosResponse<never>> =>
  api.delete(`/guests/maps/${mapId}/spaces/${spaceId}/reservations/${reservationId}`, {
    data: { password: null },
  });

export const postMemberGuestReservation = ({
  reservation,
  mapId,
  spaceId,
}: PostMemberGuestReservationParams): Promise<AxiosResponse<never>> =>
  api.post(`/guests/maps/${mapId}/spaces/${spaceId}/reservations`, reservation);

export const putMemberGuestReservation = ({
  reservation,
  mapId,
  spaceId,
  reservationId,
}: PutMemberGuestReservationParams): Promise<AxiosResponse<never>> =>
  api.put(`/guests/maps/${mapId}/spaces/${spaceId}/reservations/${reservationId}`, reservation);
