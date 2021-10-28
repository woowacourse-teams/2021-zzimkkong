import NotFoundImage from 'assets/images/notFound.png';
import Header from 'components/Header/Header';
import Layout from 'components/Layout/Layout';
import PATH from 'constants/path';
import * as Styled from './MobileRedirect.styles';

const MobileRedirect = (): JSX.Element => {
  return (
    <>
      <Header />
      <Layout>
        <Styled.Container>
          <Styled.Image src={NotFoundImage} alt="Not Found" />
          <Styled.PageHeader>에디터는 PC에서만 이용 가능합니다.</Styled.PageHeader>
          <Styled.HomeLink to={PATH.MAIN}>홈으로 이동하기</Styled.HomeLink>
        </Styled.Container>
      </Layout>
    </>
  );
};

export default MobileRedirect;
