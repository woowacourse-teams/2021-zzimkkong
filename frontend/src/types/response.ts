import { Map, Reservation, Space, SpaceReservation } from './common';

export interface ErrorResponse {
  message?: string;
  field?: string;
}

export interface LoginSuccess {
  accessToken: string;
}

export interface QueryManagerMapsSuccess {
  maps: Map[];
}

export interface QueryManagerReservationsSuccess {
  data: SpaceReservation[];
}

export interface QueryReservationsSuccess {
  reservations: Reservation[];
}

export interface QuerySpacesSuccess {
  data: Space[];
}
