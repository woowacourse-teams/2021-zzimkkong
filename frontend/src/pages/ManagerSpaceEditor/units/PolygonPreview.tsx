import { EDITOR } from 'constants/editor';
import { Coordinate } from 'types/common';

interface Props {
  points: Coordinate[];
  stickyDotCoordinate: Coordinate;
}

const PolygonPreview = ({ points, stickyDotCoordinate }: Props): JSX.Element => {
  return (
    <polygon
      points={
        points.map(({ x, y }) => `${x},${y}`).join(' ') +
        ` ${stickyDotCoordinate.x},${stickyDotCoordinate.y}`
      }
      stroke={EDITOR.STROKE_PREVIEW}
      fill={EDITOR.POLYGON_PREVIEW_FILL}
      strokeWidth={EDITOR.STROKE_WIDTH}
      pointerEvents="none"
    />
  );
};

export default PolygonPreview;
