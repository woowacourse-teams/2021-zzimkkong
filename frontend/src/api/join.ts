import { AxiosResponse } from 'axios';
import { QueryFunction } from 'react-query';
import THROW_ERROR from 'constants/throwError';
import { QueryEmojiListSuccess } from 'types/response';
import api from './api';

interface JoinParams {
  emoji: string;
  email: string;
  password: string;
  userName: string;
}

interface SocialJoinParams {
  emoji: string;
  email: string;
  userName: string;
  oauthProvider: 'GITHUB' | 'GOOGLE';
}

export interface QueryEmailParams {
  code: string;
}

export const queryValidateEmail: QueryFunction = ({ queryKey }) => {
  const [, email] = queryKey;

  if (typeof email !== 'string') throw new Error(THROW_ERROR.INVALID_EMAIL_FORMAT);

  return api.post(`/members/validations/email`, { email });
};

export const queryValidateUserName: QueryFunction = ({ queryKey }) => {
  const [, userName] = queryKey;

  if (typeof userName !== 'string') throw new Error(THROW_ERROR.INVALID_USER_NAME_FORMAT);

  return api.post(`/members/validations/username`, { userName });
};

export const postJoin = (params: JoinParams): Promise<AxiosResponse> => {
  return api.post('/members', params);
};

export const postSocialJoin = (params: SocialJoinParams): Promise<AxiosResponse> =>
  api.post(`/members/oauth`, params);

export const queryEmojiList: QueryFunction<AxiosResponse<QueryEmojiListSuccess>> = () => {
  return api.get('/members/emojis');
};
