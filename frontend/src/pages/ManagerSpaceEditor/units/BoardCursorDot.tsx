import { EDITOR } from 'constants/editor';
import { Coordinate } from 'types/common';

interface Props {
  coordinate: Coordinate;
}

const BoardCursorDot = ({ coordinate }: Props): JSX.Element => {
  return (
    <circle
      cx={coordinate.x}
      cy={coordinate.y}
      r={EDITOR.CIRCLE_CURSOR_RADIUS}
      fill={EDITOR.CIRCLE_CURSOR_FILL}
      pointerEvents="none"
    />
  );
};

export default BoardCursorDot;
