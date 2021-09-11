import { ManagerSpace } from 'types/common';
import * as Styled from './BoardSpace.styles';

interface Props {
  space: ManagerSpace;
  drawing: boolean;
  selected: boolean;
  onClick: (id: number) => void;
}

const BoardSpace = ({ space, drawing, selected, onClick }: Props): JSX.Element => {
  const { id, color, area, name } = space;

  return (
    <g>
      <Styled.SpaceRect
        x={area.x}
        y={area.y}
        width={area.width}
        height={area.height}
        fill={color}
        onClick={() => onClick(id)}
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
