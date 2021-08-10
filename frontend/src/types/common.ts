export type Color = string;

export interface Coordinate {
  x: number;
  y: number;
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

export interface MapItem {
  mapId: number;
  mapName: string;
  mapDrawing: string;
  mapImageUrl: string;
}

export interface DrawingStatus {
  start?: Coordinate;
  end?: Coordinate;
}

export interface MapElement {
  id: number;
  type: 'polyline';
  stroke: Color;
  points: string[];
}

export interface GripPoint {
  id: number;
  mapElementId: MapElement['id'];
  x: number;
  y: number;
}

export interface EditorBoard {
  width: number;
  height: number;
  x: number;
  y: number;
  scale: number;
}
