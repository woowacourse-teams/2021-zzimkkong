import { QueryFunction } from 'react-query';
import api from './api';

interface JoinProps {
  email: string;
  password: string;
  organization: string;
}

export const queryValidateEmail: QueryFunction = ({ queryKey }) => {
  const [_, email] = queryKey;

  return api.get(`/members/?email=${email}`);
};

export const postJoin = ({ email, password, organization }: JoinProps) => {
  return api.post('/members', { email, password, organization });
};
