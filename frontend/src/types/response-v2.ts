import { MapItem, Reservation, SpaceReservation } from './common';

export interface MapItemResponseV2
  extends Omit<MapItem, 'mapDrawing' | 'sharingMapId' | 'notice' | 'managerEmail'> {
  mapDrawing: string;
  slackUrl: string;
}

export interface QueryManagerMapsSuccessV2 {
  maps: MapItemResponseV2[];
  organization: string;
}

export type QueryManagerMapSuccessV2 = MapItemResponseV2;

export interface QueryManagerMapReservationsSuccessV2 {
  data: SpaceReservation[];
}

export interface QueryGuestReservationsSuccessV2 {
  reservations: Reservation[];
}
