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
  notice: string | null;
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
  reservationEnabled: boolean;
  settings: SpaceSetting[];
}

export interface Reservation {
  id: number;
  startDateTime: string;
  endDateTime: string;
  name: string;
  description: string;
  isLoginReservation: boolean;
  isMyReservation: boolean;
}

export interface MemberReservation extends Reservation {
  memberId: number;
  mapId: MapItem['mapId'];
  sharingMapId: MapItem['sharingMapId'];
  mapName: MapItem['mapName'];
  spaceId: Space['id'];
  spaceName: Space['name'];
  spaceColor: Space['color'];
}

export interface SpaceReservation {
  spaceId: number;
  spaceName: string;
  spaceColor: Color;
  reservations: Reservation[];
}

export interface SpaceSetting {
  settingStartTime: string;
  settingEndTime: string;
  reservationTimeUnit: number;
  reservationMinimumTimeUnit: number;
  reservationMaximumTimeUnit: number;
  enabledDayOfWeek: {
    monday: boolean;
    tuesday: boolean;
    wednesday: boolean;
    thursday: boolean;
    friday: boolean;
    saturday: boolean;
    sunday: boolean;
  };
  priorityOrder: number;
}

export interface Preset {
  id: number;
  name: string;
  settingStartTime: string;
  settingEndTime: string;
  reservationTimeUnit: number;
  reservationMinimumTimeUnit: number;
  reservationMaximumTimeUnit: number;
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

export interface ManagerSpace {
  id: number;
  name: string;
  color: Color;
  area: Area;
  reservationEnable: boolean;
  settings: SpaceSetting[];
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

export interface Emoji {
  name: string;
  code: string;
}
