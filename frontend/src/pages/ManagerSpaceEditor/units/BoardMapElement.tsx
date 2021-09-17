import { EDITOR } from 'constants/editor';
import { MapElement as MapElementData } from 'types/common';
import { MapElementType } from 'types/editor';

interface Props {
  mapElement: MapElementData;
}

const BoardMapElement = ({ mapElement }: Props): JSX.Element => {
  if (mapElement.type === MapElementType.Polyline) {
    return (
      <polyline
        points={mapElement.points.join(' ')}
        stroke={mapElement.stroke}
        strokeWidth={EDITOR.STROKE_WIDTH}
        strokeLinecap="round"
        pointerEvents="none"
      />
    );
  }

  return (
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
