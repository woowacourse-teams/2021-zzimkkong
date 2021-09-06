import { Link } from 'react-router-dom';
import Header from 'components/Header/Header';
import Layout from 'components/Layout/Layout';
import PATH from 'constants/path';
import LoginForm from './LoginForm';
import * as Styled from './ManagerLogin.styles';

const ManagerLogin = (): JSX.Element => {
  return (
    <>
      <Header />
      <Layout>
        <Styled.Container>
          <Styled.PageTitle>로그인</Styled.PageTitle>
          <LoginForm />
          <Styled.JoinLinkMessage>
            아직 회원이 아니신가요?
            <Link to={PATH.MANAGER_JOIN}>회원가입하기</Link>
          </Styled.JoinLinkMessage>
        </Styled.Container>
      </Layout>
    </>
  );
};

export default ManagerLogin;
