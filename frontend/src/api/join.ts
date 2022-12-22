import { AxiosResponse } from 'axios';
import { QueryFunction } from 'react-query';
import THROW_ERROR from 'constants/throwError';
import api from './api';

interface JoinParams {
  email: string;
  password: string;
  userName: string;
  organization: string;
}

interface SocialJoinParams {
  email: string;
  userName: string;
  organization: string;
  oauthProvider: 'GITHUB' | 'GOOGLE';
}

export interface QueryEmailParams {
  code: string;
}

export const queryValidateEmail: QueryFunction = ({ queryKey }) => {
  const [, email] = queryKey;

  if (typeof email !== 'string') throw new Error(THROW_ERROR.INVALID_EMAIL_FORMAT);

  return api.get(`/members?email=${email}`);
};

export const postJoin = ({
  email,
  password,
  userName,
  organization,
}: JoinParams): Promise<AxiosResponse> => {
  return api.post('/members', { email, password, userName, organization });
};

export const postSocialJoin = ({
  email,
  userName,
  organization,
  oauthProvider,
}: SocialJoinParams): Promise<AxiosResponse> =>
  api.post(`/members/oauth`, {
    email,
    userName,
    organization,
    oauthProvider,
  });
