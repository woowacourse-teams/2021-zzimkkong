import { ManagerSpaceAPI, MapItem, Reservation, Space } from './common';

export interface ErrorResponse {
  message?: string;
  field?: string;
}

export interface LoginSuccess {
  accessToken: string;
}

export interface QueryReservationsSuccess {
  reservations: Reservation[];
}

export interface QuerySpacesSuccess {
  data: Space[];
}

export interface QueryManagerSpaceSuccess {
  data: ManagerSpaceAPI;
}

export interface QueryManagerSpacesSuccess {
  spaces: ManagerSpaceAPI[];
}

export type QueryManagerMapSuccess = MapItem;
