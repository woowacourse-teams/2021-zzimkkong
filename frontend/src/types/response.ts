import { MapItem, Reservation, Space, SpaceReservation, ManagerSpaceAPI } from './common';

export interface ErrorResponse {
  message?: string;
  field?: string;
}

export interface LoginSuccess {
  accessToken: string;
}

export interface QueryManagerMapsSuccess {
  maps: MapItem[];
  organization: string;
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

export interface QueryManagerSpaceSuccess {
  data: ManagerSpaceAPI;
}

export interface QueryManagerSpacesSuccess {
  spaces: ManagerSpaceAPI[];
}

export type QueryManagerMapSuccess = MapItem;
