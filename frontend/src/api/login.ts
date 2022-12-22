import { AxiosResponse } from 'axios';
import { QueryFunction, QueryKey } from 'react-query';
import { LoginSuccess } from 'types/response';
import api from './api';

interface LoginParams {
  email: string;
  password: string;
}

export interface SocialLoginParams {
  code: string;
}

export const postLogin = (loginData: LoginParams): Promise<AxiosResponse> => {
  return api.post('/members/login/token', loginData);
};

export const postTokenValidation = (): Promise<AxiosResponse> => {
  return api.post('/members/token');
};

export const queryGithubLogin: QueryFunction<
  AxiosResponse<LoginSuccess>,
  [QueryKey, SocialLoginParams]
> = ({ queryKey }) => {
  const [, { code }] = queryKey;

  return api.get(`/members/github/login/token?code=${code}`);
};

export const queryGoogleLogin: QueryFunction<
  AxiosResponse<LoginSuccess>,
  [QueryKey, SocialLoginParams]
> = ({ queryKey }) => {
  const [, { code }] = queryKey;

  return api.get(`/members/google/login/token?code=${code}`);
};
