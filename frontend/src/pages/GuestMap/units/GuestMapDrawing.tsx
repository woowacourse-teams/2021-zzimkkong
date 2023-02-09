import React, { useContext, useMemo } from 'react';
import { EDITOR } from 'constants/editor';
import PALETTE from 'constants/palette';
import { MapDrawing, Space } from 'types/common';
import { DrawingAreaShape } from 'types/editor';
import { SpaceResponse } from 'types/response';
import { getPolygonCenterPoint } from 'utils/editor';
import { GuestMapFormContext } from '../providers/GuestMapFormProvider';
import * as Styled from './GuestMapDrawing.styles';

interface GuestMapDrawingProps {
  mapDrawing: MapDrawing;
  spaceList: Space[];
  onClickSpaceArea: (spaceId: Space['id']) => void;
}

const GuestMapDrawing = ({ mapDrawing, spaceList, onClickSpaceArea }: GuestMapDrawingProps) => {
  const { availableSpaceList } = useContext(GuestMapFormContext);

  const availableSpace = useMemo(() => {
    return (
      availableSpaceList?.reduce((acc: Record<Space['id'], SpaceResponse>, cur) => {
        acc[cur.id] = cur;

        return acc;
      }, {}) ?? {}
    );
  }, [availableSpaceList]);

  return (
    <Styled.MapItem width={mapDrawing.width} height={mapDrawing.height}>
      <svg
        xmlns="http://www.w3.org/2000/svg"
        version="1.1"
        width={mapDrawing.width}
        height={mapDrawing.height}
      >
        {/* Note: 맵을 그리는 부분 */}
        {mapDrawing.mapElements.map((element) =>
          element.type === 'polyline' ? (
            <polyline
              key={`polyline-${element.id}`}
              points={element.points.join(' ')}
              stroke={element.stroke}
              strokeWidth={EDITOR.STROKE_WIDTH}
              strokeLinecap="round"
            />
          ) : (
            <rect
              key={`rect-${element.id}`}
              x={element?.x}
              y={element?.y}
              width={element?.width}
              height={element?.height}
              stroke={element.stroke}
              fill="none"
              strokeWidth={EDITOR.STROKE_WIDTH}
            />
          )
        )}

        {/* Note: 공간을 그리는 부분 */}
        {spaceList.length > 0 &&
          spaceList.map(({ id, area, color, name }) => (
            <Styled.Space key={`area-${id}`} data-testid={id} onClick={() => onClickSpaceArea(id)}>
              {area.shape === DrawingAreaShape.Rect && (
                <>
                  <Styled.SpaceRect
                    x={area.x}
                    y={area.y}
                    width={area.width}
                    height={area.height}
                    fill={color ?? PALETTE.RED[200]}
                    opacity="0.5"
                    disabled={!availableSpace[id]}
                  />
                  <Styled.SpaceAreaText x={area.x + area.width / 2} y={area.y + area.height / 2}>
                    {name}
                  </Styled.SpaceAreaText>
                </>
              )}
              {area.shape === DrawingAreaShape.Polygon && (
                <>
                  <Styled.SpacePolygon
                    points={area.points.map(({ x, y }) => `${x},${y}`).join(' ')}
                    fill={color ?? PALETTE.RED[200]}
                    opacity="0.5"
                    disabled={!availableSpace[id]}
                  />
                  <Styled.SpaceAreaText
                    x={getPolygonCenterPoint(area.points).x}
                    y={getPolygonCenterPoint(area.points).y}
                  >
                    {name}
                  </Styled.SpaceAreaText>
                </>
              )}
            </Styled.Space>
          ))}
      </svg>
    </Styled.MapItem>
  );
};

export default GuestMapDrawing;
