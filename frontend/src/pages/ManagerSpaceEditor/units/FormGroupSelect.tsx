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
  const filteredGroups = groups.filter((group) => group.name !== 'NONE');

  const isAllUsersSelected = selectedGroups.length === 0;

  const handleAllUsersClick = () => {
    // 모든 그룹 선택 해제 (allowedGroups를 비움)
    selectedGroups.forEach((group) => onChange(group, false));
  };

  const handleGroupClick = (groupName: Group) => {
    const isSelected = selectedGroups.includes(groupName);
    onChange(groupName, !isSelected);
  };

  return (
    <Styled.Container>
      <Styled.GroupButton type="button" selected={isAllUsersSelected} onClick={handleAllUsersClick}>
        모든 사용자
      </Styled.GroupButton>
      {filteredGroups.map(({ name, displayName }) => (
        <Styled.GroupButton
          key={`group-select-${name}`}
          type="button"
          selected={selectedGroups.includes(name as Group)}
          onClick={() => handleGroupClick(name as Group)}
        >
          {displayName}
        </Styled.GroupButton>
      ))}
    </Styled.Container>
  );
};

export default FormGroupSelect;
