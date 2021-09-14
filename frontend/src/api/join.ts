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

  return api.get(`/managers/?email=${email as string}`);
};

export const postJoin = ({ email, password, organization }: JoinParams): Promise<AxiosResponse> => {
  return api.post('/managers', { email, password, organization });
};
