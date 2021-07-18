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
