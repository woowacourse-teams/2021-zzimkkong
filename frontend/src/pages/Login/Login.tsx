import { AxiosError } from 'axios';
import { FormEventHandler } from 'react';
import { useQuery } from 'react-query';
import { Link, useHistory } from 'react-router-dom';
import { postLogin } from 'api/login';
import Button from 'components/Button/Button';
import Header from 'components/Header/Header';
import Input from 'components/Input/Input';
import Layout from 'components/Layout/Layout';
import PATH from 'constants/path';
import useInput from 'hooks/useInput';
import * as Styled from './Login.styles';

const Login = (): JSX.Element => {
  const [email, onChangeEmail] = useInput('');
  const [password, onChangePassword] = useInput('');
  const history = useHistory();

  const login = useQuery(['login', { email, password }], postLogin, {
    enabled: false,
    retry: false,
  });

  const handleSubmit: FormEventHandler<HTMLFormElement> = (event) => {
    event.preventDefault();

    if (!(email && password)) return;

    login.refetch();

    if (login.isSuccess) {
      history.push(PATH.HOME);
    }
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
              minLength={8}
              onChange={onChangePassword}
              required
            />
            <Styled.LoginErrorMessage>
              {(login.error as AxiosError)?.response?.data?.message}
            </Styled.LoginErrorMessage>
            <Button variant="primary" size="large" fullWidth>
              로그인
            </Button>
          </Styled.Form>
          <Styled.JoinLinkMessage>
            아직 회원이 아니신가요?
            <Link to="/join">회원가입하기</Link>
          </Styled.JoinLinkMessage>
        </Styled.Container>
      </Layout>
    </>
  );
};

export default Login;
