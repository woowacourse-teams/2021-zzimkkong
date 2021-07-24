import { AxiosError, AxiosResponse } from 'axios';
import { FormEventHandler, useState } from 'react';
import { useMutation } from 'react-query';
import { Link, useHistory } from 'react-router-dom';
import { useSetRecoilState } from 'recoil';
import { postLogin } from 'api/login';
import Button from 'components/Button/Button';
import Header from 'components/Header/Header';
import Input from 'components/Input/Input';
import Layout from 'components/Layout/Layout';
import MANAGER from 'constants/manager';
import MESSAGE from 'constants/message';
import PATH from 'constants/path';
import { LOCAL_STORAGE_KEY } from 'constants/storage';
import useInput from 'hooks/useInput';
import accessTokenState from 'state/accessTokenState';
import { LoginSuccess } from 'types/response';
import { setLocalStorageItem } from 'utils/localStorage';
import * as Styled from './Login.styles';

const Login = (): JSX.Element => {
  const [email, onChangeEmail] = useInput('');
  const [password, onChangePassword] = useInput('');
  const [loginMessage, setLoginMessage] = useState('');

  const setAccessToken = useSetRecoilState(accessTokenState);
  const history = useHistory();

  const login = useMutation(postLogin, {
    onSuccess: (response: AxiosResponse<LoginSuccess>) => {
      const { accessToken } = response.data;

      setLocalStorageItem({ key: LOCAL_STORAGE_KEY.ACCESS_TOKEN, item: accessToken });
      setAccessToken(accessToken);

      history.push(PATH.HOME);
    },
    onError: (error: AxiosError<Error>) => {
      setLoginMessage(error.response?.data.message ?? MESSAGE.LOGIN.UNEXPECTED_ERROR);
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
        <Styled.Container>
          <Styled.PageTitle>로그인</Styled.PageTitle>
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
              minLength={MANAGER.PASSWORD.MIN_LENGTH}
              maxLength={MANAGER.PASSWORD.MAX_LENGTH}
              onChange={onChangePassword}
              required
            />
            <Styled.LoginErrorMessage>{loginMessage}</Styled.LoginErrorMessage>
            <Button variant="primary" size="large" fullWidth>
              로그인
            </Button>
            <Styled.JoinLinkMessage>
              아직 회원이 아니신가요?
              <Link to="/join">회원가입하기</Link>
            </Styled.JoinLinkMessage>
          </Styled.Form>
        </Styled.Container>
      </Layout>
    </>
  );
};

export default Login;
