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

  return api.get(`/members?email=${email}`);
};

export const queryValidateUserName: QueryFunction = ({ queryKey }) => {
  const [, userName] = queryKey;

  if (typeof userName !== 'string') throw new Error(THROW_ERROR.INVALID_USER_NAME_FORMAT);

  return api.get(`/members?userName=${userName}`);
};

export const postJoin = ({
  emoji,
  email,
  password,
  userName,
}: JoinParams): Promise<AxiosResponse> => {
  return api.post('/members', { emoji, email, password, userName });
};

export const postSocialJoin = ({
  emoji,
  email,
  userName,
  oauthProvider,
}: SocialJoinParams): Promise<AxiosResponse> =>
  api.post(`/members/oauth`, { emoji, email, userName, oauthProvider });

export const getEmojiList: QueryFunction<AxiosResponse<QueryEmojiListSuccess>> = () => {
  return api.get('/members/emojis');
};
