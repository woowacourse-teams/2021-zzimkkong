import { useQuery } from 'react-query';
import { queryGroups } from 'api/group';
import { Group } from 'types/common';
import * as Styled from './FormGroupSelect.styles';

interface Props {
  selectedGroups: Group[];
  onChange: (group: Group, checked: boolean) => void;
}

const FormGroupSelect = ({ selectedGroups, onChange }: Props): JSX.Element => {
  const { data: groupsResponse, isLoading } = useQuery('groups', queryGroups);

  if (isLoading || !groupsResponse) {
    return <Styled.Container>Loading...</Styled.Container>;
  }

  const groups = groupsResponse.data;

  return (
    <Styled.Container>
      {groups.map(({ name, displayName, color }) => (
        <Styled.Label key={`group-select-${name}`} borderColor={color}>
          <Styled.DisplayName>{displayName}</Styled.DisplayName>
          <input
            type="checkbox"
            checked={selectedGroups.includes(name as Group)}
            onChange={(event) => onChange(name as Group, event.target.checked)}
          />
        </Styled.Label>
      ))}
    </Styled.Container>
  );
};

export default FormGroupSelect;
