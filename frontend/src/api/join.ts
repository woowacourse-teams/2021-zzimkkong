import { AxiosResponse } from 'axios';
import { QueryFunction } from 'react-query';
import api from './api';

interface JoinParams {
  email: string;
  password: string;
  organization: string;
}

interface SocialJoinParams {
  email: string;
  organization: string;
  oauthProvider: 'GITHUB' | 'GOOGLE';
}

export interface QueryEmailParams {
  code: string;
}

export const queryValidateEmail: QueryFunction = ({ queryKey }) => {
  const [, email] = queryKey;

  return api.get(`/managers?email=${email as string}`);
};

export const postJoin = ({ email, password, organization }: JoinParams): Promise<AxiosResponse> => {
  return api.post('/managers', { email, password, organization });
};

export const postSocialJoin = ({
  email,
  organization,
  oauthProvider,
}: SocialJoinParams): Promise<AxiosResponse> =>
  api.post(`/api/managers/oauth`, {
    email,
    organization,
    oauthProvider,
  });
