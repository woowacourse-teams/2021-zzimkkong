import { Link } from 'react-router-dom';
import Button from 'components/Button/Button';
import Header from 'components/Header/Header';
import Input from 'components/Input/Input';
import Layout from 'components/Layout/Layout';
import * as Styled from './Login.styles';

const Login = (): JSX.Element => {
  return (
    <>
      <Header />
      <Layout>
        <Styled.PageTitle>로그인</Styled.PageTitle>
        <Styled.Container>
          <Styled.Form>
            <Input type="email" label="이메일" autoFocus />
            <Input type="password" label="비밀번호" />
            <Button variant="primary" size="large" fullWidth>
              로그인
            </Button>
          </Styled.Form>
          <Styled.JoinMessage>
            아직 회원이 아니신가요?
            <Link to="/join">회원가입하기</Link>
          </Styled.JoinMessage>
        </Styled.Container>
      </Layout>
    </>
  );
};

export default Login;
