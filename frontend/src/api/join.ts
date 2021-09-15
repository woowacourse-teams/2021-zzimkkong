import { AxiosResponse } from 'axios';
import { QueryFunction } from 'react-query';
import api from './api';

interface JoinParams {
  email: string;
  password: string;
  organization: string;
}

export const queryValidateEmail: QueryFunction = ({ queryKey }) => {
  const [, email] = queryKey;

  if (typeof email !== 'string') throw new Error('Email은 string 형식이어야 합니다.');

  return api.get(`/managers?email=${email}`);
};

export const postJoin = ({ email, password, organization }: JoinParams): Promise<AxiosResponse> => {
  return api.post('/managers', { email, password, organization });
};
