import React from 'react';
import PALETTE from 'constants/palette';
import { Color, Coordinate } from 'types/common';

interface Props {
  coordinate: Coordinate;
  size: number;
  color?: Color;
}

const CursorRect = ({
  coordinate,
  size,
  color = PALETTE.OPACITY_BLACK[100],
}: Props): JSX.Element => {
  return <rect x={coordinate.x} y={coordinate.y} width={size} height={size} fill={color} />;
};

export default CursorRect;
