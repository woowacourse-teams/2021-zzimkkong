import PALETTE from './palette';

export enum Mode {
  SELECT = 'select',
  MOVE = 'move',
  LINE = 'line',
  POLYLINE = 'polyline',
  DECORATION = 'decoration',
}

export enum DrawingAreaShape {
  RECT = 'rect',
  POLYGON = 'polygon',
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

export const KEY = {
  DELETE: 'Delete',
  BACK_SPACE: 'Backspace',
  SPACE: ' ',
};

export const EDITOR = {
  GRID_SIZE: 10,
  SCALE_DELTA: 0.001,
  MIN_SCALE: 0.5,
  MAX_SCALE: 3.0,
  STROKE_WIDTH: 3,
  STROKE_PREVIEW: PALETTE.OPACITY_BLACK[200],
  OPACITY: 1,
  OPACITY_DELETING: 0.3,
  TEXT_OPACITY: 0.3,
  TEXT_FONT_SIZE: '1rem',
  TEXT_FILL: PALETTE.BLACK[700],
  SPACE_OPACITY: 0.1,
  CIRCLE_CURSOR_RADIUS: 3,
  CIRCLE_CURSOR_FILL: PALETTE.OPACITY_BLACK[300],
};
