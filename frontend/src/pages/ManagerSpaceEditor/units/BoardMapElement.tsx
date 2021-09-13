import { EDITOR } from 'constants/editor';
import { MapElement as MapElementType } from 'types/common';

interface Props {
  mapElement: MapElementType;
}

const BoardMapElement = ({ mapElement }: Props): JSX.Element => {
  return mapElement.type === 'polyline' ? (
    <polyline
      points={mapElement.points.join(' ')}
      stroke={mapElement.stroke}
      strokeWidth={EDITOR.STROKE_WIDTH}
      strokeLinecap="round"
      pointerEvents="none"
    />
  ) : (
    <rect
      x={mapElement?.x}
      y={mapElement?.y}
      width={mapElement?.width}
      height={mapElement?.height}
      stroke={mapElement.stroke}
      fill="none"
      strokeWidth={EDITOR.STROKE_WIDTH}
      strokeLinecap="round"
    />
  );
};

export default BoardMapElement;
