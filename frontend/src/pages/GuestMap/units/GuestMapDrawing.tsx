import React, { useContext, useMemo } from 'react';
import { EDITOR } from 'constants/editor';
import PALETTE from 'constants/palette';
import { MapDrawing, Group, Space } from 'types/common';
import { DrawingAreaShape } from 'types/editor';
import { SpaceResponse } from 'types/response';
import { getPolygonCenterPoint } from 'utils/editor';
import { GuestMapFormContext } from '../providers/GuestMapFormProvider';
import * as Styled from './GuestMapDrawing.styles';

interface GuestMapDrawingProps {
  mapDrawing: MapDrawing;
  spaceList: Space[];
  onClickSpaceArea: (spaceId: Space['id']) => void;
  // 예약하기에서 조회된 맵인지 예약현황에서 조횐된 맵인지 판별하기 위한 flag
  isReservation: boolean;
  userGroup: Group | null;
}

const GuestMapDrawing = ({
  mapDrawing,
  spaceList,
  onClickSpaceArea,
  isReservation,
  userGroup,
}: GuestMapDrawingProps) => {
  const { availableSpaceList } = useContext(GuestMapFormContext);

  const availableSpace = useMemo(() => {
    return (
      availableSpaceList?.reduce((acc: Record<Space['id'], SpaceResponse>, cur) => {
        acc[cur.id] = cur;

        return acc;
      }, {}) ?? {}
    );
  }, [availableSpaceList]);

  const hasPermission = (space: Space): boolean => {
    // If no groups are specified, everyone can reserve
    if (!space.allowedGroups || space.allowedGroups.length === 0) {
      return true;
    }

    // If user is not logged in (no group), they cannot reserve group-restricted spaces
    if (!userGroup) {
      return false;
    }

    // Check if user's group is in the allowed groups
    return space.allowedGroups.includes(userGroup);
  };

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
          spaceList.map((space) => {
            const { id, area, color, name } = space;
            const isDisabled = isReservation && (!availableSpace[id] || !hasPermission(space));

            return (
              <Styled.Space
                key={`area-${id}`}
                data-testid={id}
                onClick={() => onClickSpaceArea(id)}
              >
                {area.shape === DrawingAreaShape.Rect && (
                  <>
                    <Styled.SpaceRect
                      x={area.x}
                      y={area.y}
                      width={area.width}
                      height={area.height}
                      fill={color ?? PALETTE.RED[200]}
                      opacity="0.5"
                      disabled={isDisabled}
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
                      disabled={isDisabled}
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
            );
          })}
      </svg>
    </Styled.MapItem>
  );
};

export default GuestMapDrawing;
