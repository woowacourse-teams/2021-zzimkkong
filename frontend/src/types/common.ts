import React from 'react';
import { DrawingAreaShape } from 'types/editor';
import { MapElementType } from './editor';

export type Color = string;

export enum Order {
  Ascending = 'ascending',
  Descending = 'descending',
}

export enum ReservationStatus {
  using = '사용 중',
  done = '사용 완료',
}

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
  type: MapElementType;
  width?: number;
  height?: number;
  x?: number;
  y?: number;
  stroke: Color;
  points: string[];
  ref: React.MutableRefObject<SVGPolylineElement | SVGRectElement | null>;
}

export interface MapItem {
  mapId: number;
  mapName: string;
  mapDrawing: MapDrawing;
  thumbnail: string;
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

export interface AreaRect {
  shape: DrawingAreaShape.Rect;
  width: number;
  height: number;
  x: number;
  y: number;
}

export interface AreaPolygon {
  shape: DrawingAreaShape.Polygon;
  points: Coordinate[];
}

export type Area = AreaRect | AreaPolygon;

export interface Space {
  id: number;
  name: string;
  color: Color;
  description: string;
  area: AreaRect | AreaPolygon;
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
  enabledDayOfWeek: {
    monday: boolean;
    tuesday: boolean;
    wednesday: boolean;
    thursday: boolean;
    friday: boolean;
    saturday: boolean;
    sunday: boolean;
  };
}

export interface Preset extends ReservationSettings {
  id: number;
  name: string;
}

export interface ManagerSpace {
  id: number;
  name: string;
  color: Color;
  description: string;
  area: Area;
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
  mapElements: Omit<MapElement[], 'ref'>;
}

export interface GripPoint {
  id: number;
  mapElement: MapElement;
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
