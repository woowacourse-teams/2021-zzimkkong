import { Link } from 'react-router-dom';
import Button from 'components/Button/Button';
import PATH, { HREF } from 'constants/path';
import * as Styled from './EditorHeader.styles';

interface Props {
  mapId: number;
  mapName: string;
}

// TODO: Link태그 내 Button 태그 제거
const EditorHeader = ({ mapId, mapName }: Props): JSX.Element => {
  return (
    <Styled.Container>
      <Styled.MapName>{mapName}</Styled.MapName>
      <Styled.ButtonContainer>
        <Link to={HREF.MANAGER_MAP_DETAIL(mapId)}>
          <Button variant="primary">공간 편집 끝내기</Button>
        </Link>
      </Styled.ButtonContainer>
    </Styled.Container>
  );
};

export default EditorHeader;
