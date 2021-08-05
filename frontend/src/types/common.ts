import { DrawingAreaShape } from 'constants/editor';

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
  publicMapId: string;
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

export interface ReservationSettings {
  availableStartTime: string;
  availableEndTime: string;
  reservationTimeUnit: number;
  reservationMinimumTimeUnit: number;
  reservationMaximumTimeUnit: number;
  reservationEnable: boolean;
  enabledDayOfWeek: string | null;
}

export interface ManagerSpace {
  id: number;
  name: string;
  color: Color;
  description: string;
  area: SpaceArea;
  settings: ReservationSettings;
}

export interface ManagerSpaceAPI extends Omit<ManagerSpace, 'area'> {
  area: string;
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

export interface MapDrawing {
  width: number;
  height: number;
  mapElements: MapElement[];
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

export interface MapDrawing {
  width: number;
  height: number;
  mapElements: MapElement[];
}

export interface SpaceArea {
  shape: DrawingAreaShape;
  x: number;
  y: number;
  width: number;
  height: number;
}

export interface DrawingStatus {
  start?: Coordinate;
  end?: Coordinate;
}
