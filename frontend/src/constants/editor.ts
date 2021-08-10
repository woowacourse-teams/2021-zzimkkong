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
  PALETTE.ORANGE[400],
  PALETTE.RED[400],
  PALETTE.YELLOW[500],
  PALETTE.GREEN[400],
  PALETTE.BLUE[400],
  PALETTE.PURPLE[400],
];
