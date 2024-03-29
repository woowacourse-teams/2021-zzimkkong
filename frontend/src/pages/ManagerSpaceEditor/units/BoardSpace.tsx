import { ManagerSpace } from 'types/common';
import { DrawingAreaShape } from 'types/editor';
import { getPolygonCenterPoint } from 'utils/editor';
import * as Styled from './BoardSpace.styles';

interface Props {
  space: Omit<ManagerSpace, 'reservationEnable' | 'id' | 'description' | 'settings'>;
  drawing: boolean;
  selected: boolean;
  onClick?: () => void;
}

const BoardSpace = ({ space, drawing, selected, onClick }: Props): JSX.Element => {
  const { color, area, name } = space;

  if (area.shape === DrawingAreaShape.Polygon) {
    const centerPoint = getPolygonCenterPoint(area.points);

    return (
      <g>
        <Styled.SpacePolygon
          points={area.points.map(({ x, y }) => `${x},${y}`).join(' ')}
          fill={color}
          onClick={onClick}
          disabled={drawing || selected}
          selected={selected}
        />
        <Styled.SpaceText x={centerPoint.x} y={centerPoint.y}>
          {name}
        </Styled.SpaceText>
      </g>
    );
  }

  return (
    <g>
      <Styled.SpaceRect
        x={area.x}
        y={area.y}
        width={area.width}
        height={area.height}
        fill={color}
        onClick={onClick}
        disabled={drawing || selected}
        selected={selected}
      />
      <Styled.SpaceText x={area.x + area.width / 2} y={area.y + area.height / 2}>
        {name}
      </Styled.SpaceText>
    </g>
  );
};

export default BoardSpace;
