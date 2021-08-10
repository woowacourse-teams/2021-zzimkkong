export type Color = string;

export interface Coordinate {
  x: number;
  y: number;
}

export interface ScrollPosition {
  x?: number;
  y?: number;
}

export interface MapItem {
  mapId: number;
  mapName: string;
  mapDrawing: string;
  mapImageUrl: string;
}

export interface Reservation {
  id: number;
  startDateTime: string;
  endDateTime: string;
  name: string;
  description: string;
}

export interface Space {
  spaceId: number;
  spaceName: string;
  spaceColor: Color;
  textPosition: 'left' | 'right' | 'top' | 'bottom';
  coordinate: Coordinate;
}

export interface SpaceReservation extends Space {
  reservations: Reservation[];
}
