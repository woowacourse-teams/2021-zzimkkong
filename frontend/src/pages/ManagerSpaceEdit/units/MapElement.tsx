import { EDITOR } from 'constants/editor';
import { MapElement as MapElementType } from 'types/common';

interface Props {
  mapElements: MapElementType[];
}

const MapElement = ({ mapElements }: Props): JSX.Element | null => {
  if (!mapElements || mapElements.length === 0) return null;

  return (
    <>
      {mapElements?.map((element) =>
        element.type === 'polyline' ? (
          <polyline
            key={`${element.id}`}
            points={element.points.join(' ')}
            stroke={element.stroke}
            strokeWidth={EDITOR.STROKE_WIDTH}
            strokeLinecap="round"
            pointerEvents="none"
          />
        ) : (
          <rect
            key={`square-${element.id}`}
            x={element?.x}
            y={element?.y}
            width={element?.width}
            height={element?.height}
            stroke={element.stroke}
            fill="none"
            strokeWidth={EDITOR.STROKE_WIDTH}
            strokeLinecap="round"
          />
        )
      )}
    </>
  );
};

export default MapElement;
