import { Dispatch, ReactNode, SetStateAction, useMemo } from 'react';
import Select from 'components/Select/Select';
import { ManagerSpace } from 'types/common';
import { sortSpaces } from 'utils/sort';
import ColorDot from './ColorDot';
import * as Styled from './SpaceSelect.styles';

interface Props {
  spaces: ManagerSpace[];
  selectedSpaceIdState: [number | null, Dispatch<SetStateAction<number | null>>];
  disabled: boolean;
  children: ReactNode;
}

const SpaceSelect = ({ spaces, selectedSpaceIdState, disabled, children }: Props): JSX.Element => {
  const [selectedSpaceId, setSelectedSpaceId] = selectedSpaceIdState;

  const sortedSpaces = useMemo(() => sortSpaces(spaces), [spaces]);

  const spaceOptions = sortedSpaces.map(({ id, name, color }) => ({
    value: `${id}`,
    children: (
      <Styled.SpaceOption>
        <ColorDot size="medium" color={color} />
        {name}
      </Styled.SpaceOption>
    ),
  }));

  return (
    <Styled.SpaceSelect>
      <Styled.FormHeader>공간 선택</Styled.FormHeader>
      <Styled.SpaceSelectWrapper>
        <Select
          name="space"
          label="공간 선택"
          options={spaceOptions}
          disabled={disabled}
          value={`${selectedSpaceId ?? ''}`}
          onChange={(id) => setSelectedSpaceId(Number(id))}
        />
      </Styled.SpaceSelectWrapper>
      {children}
    </Styled.SpaceSelect>
  );
};

export default SpaceSelect;
