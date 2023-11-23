import { ManagerSpaceAPIV2, MapItem, Reservation, Space, SpaceReservation } from './common';

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

export type QueryGuestMapSuccessV2 = MapItemResponseV2;
export interface QueryManagerMapReservationsSuccessV2 {
  data: SpaceReservation[];
}

export interface QueryGuestReservationsSuccessV2 {
  reservations: Reservation[];
}

export interface QuerySpacesSuccessV2 {
  spaces: SpaceResponseV2[];
}

export type QueryGuestSpaceSuccessV2 = ManagerSpaceAPIV2;

export interface SpaceResponseV2 extends Omit<Space, 'area'> {
  area: string;
}

// export interface Space {
//   id: number;
//   name: string;
//   color: Color;
//   description: string;
//   area: AreaRect | AreaPolygon;
//   reservationEnabled: boolean;
//   settings: SpaceSetting[];
// }
