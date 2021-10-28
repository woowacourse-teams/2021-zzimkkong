import NotFoundImage from 'assets/images/notFound.png';
import PATH from 'constants/path';
import * as Styled from './MobileRedirect.styles';

const MobileRedirect = (): JSX.Element => {
  return (
    <Styled.Container>
      <Styled.Image src={NotFoundImage} alt="Not Found" />
      <Styled.PageHeader>에디터는 PC에서만 이용 가능합니다.</Styled.PageHeader>
      <Styled.HomeLink to={PATH.MANAGER_MAIN}>이전 페이지로 이동하기</Styled.HomeLink>
    </Styled.Container>
  );
};

export default MobileRedirect;
