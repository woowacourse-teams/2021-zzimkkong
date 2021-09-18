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
  return api.post('/managers/login/token', loginData);
};

export const queryGithubLogin: QueryFunction<
  AxiosResponse<LoginSuccess>,
  [QueryKey, SocialLoginParams]
> = ({ queryKey }) => {
  const [, { code }] = queryKey;

  return api.get(`/managers/github/login/token?code=${code}`);
};

export const queryGoogleLogin: QueryFunction<
  AxiosResponse<LoginSuccess>,
  [QueryKey, SocialLoginParams]
> = ({ queryKey }) => {
  const [, { code }] = queryKey;

  return api.get(`/managers/google/login/token?code=${code}`);
};
