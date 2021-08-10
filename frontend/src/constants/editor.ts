import PALETTE from './palette';

export enum Mode {
  SELECT = 'select',
  MOVE = 'move',
  LINE = 'line',
  POLYLINE = 'polyline',
  DECORATION = 'decoration',
}

export const MAP_COLOR_PALETTE = [
  PALETTE.BLACK[400],
  PALETTE.GRAY[400],
  PALETTE.RED[900],
  PALETTE.ORANGE[900],
  PALETTE.YELLOW[900],
  PALETTE.GREEN[900],
  PALETTE.BLUE[900],
  PALETTE.PURPLE[900],
];

export const BOARD = {
  MAX_WIDTH: 5000,
  MIN_WIDTH: 100,
  MAX_HEIGHT: 5000,
  MIN_HEIGHT: 100,
};
