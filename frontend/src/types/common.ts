import { DrawingAreaShape } from 'constants/editor';

export type Color = string;

export type Order = 'ascending' | 'descending';

export interface Coordinate {
  x: number;
  y: number;
}

export interface ScrollPosition {
  x?: number;
  y?: number;
}
export interface MapElement {
  id: number;
  type: 'polyline' | 'rect';
  width?: number;
  height?: number;
  x?: number;
  y?: number;
  stroke: Color;
  points: string[];
}

export interface MapItem {
  mapId: number;
  mapName: string;
  mapDrawing: MapDrawing;
  mapImageUrl: string;
  sharingMapId: string;
}

interface SpaceSetting {
  availableStartTime: string;
  availableEndTime: string;
  reservationTimeUnit: number;
  reservationMinimumTimeUnit: number;
  reservationMaximumTimeUnit: number;
  reservationEnable: boolean;
  disabledWeekdays: string[];
}

export interface Area {
  shape: string;
  width: number;
  height: number;
  x: number;
  y: number;
}

export interface Space {
  id: number;
  name: string;
  color: Color;
  description: string;
  area: Area;
  settings: SpaceSetting;
}

export interface Reservation {
  id: number;
  startDateTime: string;
  endDateTime: string;
  name: string;
  description: string;
}

export interface SpaceReservation {
  spaceId: number;
  spaceName: string;
  spaceColor: Color;
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

export interface DrawingStatus {
  start?: Coordinate;
  end?: Coordinate;
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

export interface SpaceArea {
  shape: DrawingAreaShape;
  x: number;
  y: number;
  width: number;
  height: number;
}
