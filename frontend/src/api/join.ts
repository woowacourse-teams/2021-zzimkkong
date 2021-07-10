import { MutateFunction, QueryFunction } from 'react-query';
import api from './api';

interface PostJoinProps {
  email: string;
  password: string;
  organization: string;
}

export const getValidateEmail: QueryFunction = ({ queryKey }) => {
  const [, email] = queryKey;

  return api.get(`/members/?email=${email}`);
};

export const postJoin = ({ email, password, organization }: PostJoinProps) => {
  return api.post('/members', { email, password, organization });
};
