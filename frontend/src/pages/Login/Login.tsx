import { AxiosError } from 'axios';
import { FormEventHandler, useState } from 'react';
import { useMutation } from 'react-query';
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
  const [loginMessage, setLoginMessage] = useState('');
  const history = useHistory();

  const login = useMutation(postLogin, {
    onSuccess: () => {
      history.push(PATH.HOME);
    },
    onError: (error) => {
      setLoginMessage((error as AxiosError)?.response?.data.message);
    },
  });

  const handleSubmit: FormEventHandler<HTMLFormElement> = (event) => {
    event.preventDefault();

    if (!(email && password)) return;

    login.mutate({ email, password });
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
            <Styled.LoginErrorMessage>{loginMessage}</Styled.LoginErrorMessage>
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
