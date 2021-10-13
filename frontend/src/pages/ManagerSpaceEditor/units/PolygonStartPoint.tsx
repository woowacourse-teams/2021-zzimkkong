import { EDITOR } from 'constants/editor';
import { Coordinate } from 'types/common';

interface Props {
  coordinate: Coordinate;
}

const PolygonStartPoint = ({ coordinate }: Props): JSX.Element => {
  return (
    <circle
      cx={coordinate.x}
      cy={coordinate.y}
      r={EDITOR.CIRCLE_PREVIEW_RADIUS}
      fill={EDITOR.CIRCLE_CURSOR_FILL}
      pointerEvents="none"
    />
  );
};

export default PolygonStartPoint;
