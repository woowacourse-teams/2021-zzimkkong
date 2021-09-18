import { AxiosError, AxiosResponse } from 'axios';
import { useEffect, useState } from 'react';
import { useMutation } from 'react-query';
import { useHistory } from 'react-router';
import { Link } from 'react-router-dom';
import { useSetRecoilState } from 'recoil';
import { postLogin, postTokenValidation } from 'api/login';
import Header from 'components/Header/Header';
import Layout from 'components/Layout/Layout';
import SocialLoginButton from 'components/SocialAuthButton/SocialLoginButton';
import MESSAGE from 'constants/message';
import PATH from 'constants/path';
import { LOCAL_STORAGE_KEY } from 'constants/storage';
import accessTokenState from 'state/accessTokenState';
import { ErrorResponse, LoginSuccess } from 'types/response';
import { setLocalStorageItem } from 'utils/localStorage';
import * as Styled from './ManagerLogin.styles';
import LoginForm from './units/LoginForm';

export interface ErrorMessage {
  email?: string;
  password?: string;
}

export interface LoginParams {
  email: string;
  password: string;
}

const ManagerLogin = (): JSX.Element => {
  const history = useHistory();

  const setAccessToken = useSetRecoilState(accessTokenState);

  const [errorMessage, setErrorMessage] = useState<ErrorMessage>({
    email: '',
    password: '',
  });

  const { isError: tokenValidation } = useMutation(postTokenValidation);

  if (!tokenValidation) {
    setAccessToken('');
  }

  const login = useMutation(postLogin, {
    onSuccess: (response: AxiosResponse<LoginSuccess>) => {
      const { accessToken } = response.data;

      setLocalStorageItem({ key: LOCAL_STORAGE_KEY.ACCESS_TOKEN, item: accessToken });
      setAccessToken(accessToken);

      history.push(PATH.MANAGER_MAIN);
    },
    onError: (error: AxiosError<ErrorResponse>) => {
      const field = error.response?.data.field;
      const message = error.response?.data.message;

      if (field && message) {
        setErrorMessage({ [field]: message });
        return;
      }

      setErrorMessage({ password: message ?? MESSAGE.LOGIN.UNEXPECTED_ERROR });
    },
  });

  const handleSubmit = ({ email, password }: LoginParams) => {
    if (!(email && password)) return;

    login.mutate({ email, password });
  };

  return (
    <>
      <Header />
      <Layout>
        <Styled.Container>
          <Styled.PageTitle>로그인</Styled.PageTitle>
          <LoginForm errorMessage={errorMessage} onSubmit={handleSubmit} />
          <Styled.HorizontalLine />
          <Styled.SocialLogin>
            <SocialLoginButton provider="GITHUB" href={PATH.GITHUB_LOGIN} />
            <SocialLoginButton provider="GOOGLE" href={PATH.GOOGLE_LOGIN} />
          </Styled.SocialLogin>
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
