import { FormEventHandler } from 'react';
import { useQuery } from 'react-query';
import { Link } from 'react-router-dom';
import { postLogin } from 'api/login';
import Button from 'components/Button/Button';
import Header from 'components/Header/Header';
import Input from 'components/Input/Input';
import Layout from 'components/Layout/Layout';
import useInput from 'hooks/useInput';
import * as Styled from './Login.styles';

const Login = (): JSX.Element => {
  const [email, onChangeEmail] = useInput('');
  const [password, onChangePassword] = useInput('');

  const login = useQuery(['login', { email, password }], postLogin, {
    enabled: false,
    retry: false,
  });

  const handleSubmit: FormEventHandler = (event) => {
    event.preventDefault();

    if (!(email && password)) return;

    login.refetch();
  };

  return (
    <>
      <Header />
      <Layout>
        <Styled.PageTitle>로그인</Styled.PageTitle>
        <Styled.Container>
          <Styled.Form onSubmit={handleSubmit}>
            <Input
              type="email"
              label="이메일"
              value={email}
              onChange={onChangeEmail}
              autoFocus
              required
            />
            <Input
              type="password"
              label="비밀번호"
              value={password}
              onChange={onChangePassword}
              required
            />
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
