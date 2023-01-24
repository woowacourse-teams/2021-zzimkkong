import { AxiosError, AxiosResponse } from 'axios';
import { useContext, useEffect, useState } from 'react';
import { useMutation } from 'react-query';
import { useHistory } from 'react-router';
import { Link } from 'react-router-dom';
import { postLogin, postTokenValidation } from 'api/login';
import Header from 'components/Header/Header';
import Layout from 'components/Layout/Layout';
import SocialLoginButton from 'components/SocialAuthButton/SocialLoginButton';
import MESSAGE from 'constants/message';
import PATH from 'constants/path';
import { AccessTokenContext } from 'providers/AccessTokenProvider';
import { ErrorResponse, LoginSuccess } from 'types/response';
import * as Styled from './Login.styles';
import LoginForm from './units/LoginForm';

export interface ErrorMessage {
  email?: string;
  password?: string;
}

export interface LoginParams {
  email: string;
  password: string;
}

const Login = (): JSX.Element => {
  const history = useHistory();

  const { setAccessToken, resetAccessToken } = useContext(AccessTokenContext);

  const [errorMessage, setErrorMessage] = useState<ErrorMessage>({
    email: '',
    password: '',
  });

  const validateToken = useMutation(postTokenValidation, {
    onError: () => {
      resetAccessToken();
    },
  });

  const login = useMutation(postLogin, {
    onSuccess: (response: AxiosResponse<LoginSuccess>) => {
      const { accessToken } = response.data;

      setAccessToken(accessToken);

      history.push(PATH.MANAGER_MAP_LIST);
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

  useEffect(() => {
    validateToken.mutate();
  }, []);

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

export default Login;
