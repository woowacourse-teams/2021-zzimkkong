import { Reservation, Space } from './common';

export interface LoginSuccess {
  accessToken: string;
}

export interface QueryReservationsSuccess {
  reservations: Reservation[];
}

export interface QuerySpacesSuccess {
  data: Space[];
}
