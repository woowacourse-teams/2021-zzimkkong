import { Dispatch, ReactNode, SetStateAction, useMemo } from 'react';
import ColorDot from 'components/ColorDot/ColorDot';
import Select from 'components/Select/Select';
import useFormContext from 'hooks/useFormContext';
import { ManagerSpace } from 'types/common';
import { sortSpaces } from 'utils/sort';
import { SpaceFormContext } from '../providers/SpaceFormProvider';
import * as Styled from './SpaceSelect.styles';

interface Props {
  spaces: ManagerSpace[];
  selectedSpaceIdState: [number | null, Dispatch<SetStateAction<number | null>>];
  disabled: boolean;
  children: ReactNode;
}

const SpaceSelect = ({ spaces, selectedSpaceIdState, disabled, children }: Props): JSX.Element => {
  const [selectedSpaceId, setSelectedSpaceId] = selectedSpaceIdState;

  const { updateWithSpace } = useFormContext(SpaceFormContext);

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

  const handleChangeSpace = (spaceId: number) => {
    const selectedSpace = spaces.find((space) => space.id === spaceId);

    updateWithSpace(selectedSpace as ManagerSpace);
    setSelectedSpaceId(spaceId);
  };

  return (
    <Styled.SpaceSelect>
      <Styled.Title>공간 선택</Styled.Title>
      <Styled.SpaceSelectWrapper>
        <Select
          name="space"
          label="공간 선택"
          options={spaceOptions}
          disabled={disabled}
          value={`${selectedSpaceId ?? ''}`}
          onChange={(id) => handleChangeSpace(Number(id))}
        />
      </Styled.SpaceSelectWrapper>
      {children}
    </Styled.SpaceSelect>
  );
};

export default SpaceSelect;
