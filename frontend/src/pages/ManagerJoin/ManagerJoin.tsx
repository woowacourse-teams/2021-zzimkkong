import { Link } from 'react-router-dom';
import Header from 'components/Header/Header';
import Layout from 'components/Layout/Layout';
import PATH from 'constants/path';
import * as Styled from './ManagerJoin.styles';
import JoinForm from './units/JoinForm';

const ManagerJoin = (): JSX.Element => {
  return (
    <>
      <Header />
      <Layout>
        <Styled.Container>
          <Styled.PageTitle>회원가입</Styled.PageTitle>
          <JoinForm />
          <Styled.JoinLinkMessage>
            이미 회원이신가요?
            <Link to={PATH.MANAGER_LOGIN}>로그인하기</Link>
          </Styled.JoinLinkMessage>
        </Styled.Container>
      </Layout>
    </>
  );
};

export default ManagerJoin;
