import { EDITOR } from 'constants/editor';
import { ManagerSpace } from 'types/common';
import { WithOptional } from 'types/util';
import * as Styled from './BoardSpace.styles';

interface Props {
  space: WithOptional<ManagerSpace, 'id' | 'description' | 'settings'>;
  drawing: boolean;
  selected: boolean;
  onClick?: () => void;
}

const BoardSpace = ({ space, drawing, selected, onClick }: Props): JSX.Element => {
  const { color, area, name } = space;

  if (area.shape !== 'rect')
    return (
      <g>
        <Styled.SpacePolygon
          points={area.points.map(({ x, y }) => `${x},${y}`).join(' ')}
          stroke={EDITOR.STROKE_PREVIEW}
          fill={color}
          strokeWidth={EDITOR.STROKE_WIDTH}
          pointerEvents="none"
          onClick={onClick}
          disabled={drawing || selected}
          selected={selected}
        />
        <Styled.SpaceText>{name}</Styled.SpaceText>
      </g>
    );

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
