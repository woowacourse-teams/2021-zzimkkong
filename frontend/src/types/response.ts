import { MapItem, Reservation, Space, SpaceReservation, ManagerSpaceAPI } from './common';

export interface MapItemResponse extends Omit<MapItem, 'mapDrawing'> {
  mapDrawing: string;
}

interface SpaceResponse extends Omit<Space, 'area'> {
  area: string;
}

export interface ErrorResponse {
  message?: string;
  field?: string;
}

export interface LoginSuccess {
  accessToken: string;
}

export type QueryGuestMapSuccess = MapItemResponse;

export type QueryManagerMapSuccess = MapItemResponse;

export interface QueryManagerMapsSuccess {
  maps: MapItemResponse[];
  organization: string;
}

export interface QueryManagerReservationsSuccess {
  data: SpaceReservation[];
}

export interface QueryReservationsSuccess {
  reservations: Reservation[];
}

export interface QuerySpacesSuccess {
  spaces: SpaceResponse[];
}

export interface QueryManagerSpaceSuccess {
  data: ManagerSpaceAPI;
}

export interface QueryManagerSpacesSuccess {
  spaces: ManagerSpaceAPI[];
}
