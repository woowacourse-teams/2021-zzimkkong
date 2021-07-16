import { Reservation } from './common';

export interface LoginSuccess {
  accessToken: string;
}

export interface QueryReservationsSuccess {
  reservations: Reservation[];
}
