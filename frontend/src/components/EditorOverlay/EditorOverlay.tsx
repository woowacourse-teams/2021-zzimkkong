import GrayLogoImage from 'assets/images/gray-logo.png';
import PATH from 'constants/path';
import * as Styled from './EditorOverlay.styles';

const EditorOverlay = (): JSX.Element => {
  return (
    <Styled.Container>
      <Styled.Image src={GrayLogoImage} alt="Not Found" />
      <Styled.PageHeader>에디터는 PC에서 이용 가능합니다.</Styled.PageHeader>
      <Styled.HomeLink to={PATH.MANAGER_MAIN}>이전 페이지로 이동하기</Styled.HomeLink>
    </Styled.Container>
  );
};

export default EditorOverlay;
