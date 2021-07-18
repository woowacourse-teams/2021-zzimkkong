import { Color } from './styled';

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
  color: Color;
  textPosition: 'left' | 'right' | 'top' | 'bottom';
  coordinate: Coordinate;
}
