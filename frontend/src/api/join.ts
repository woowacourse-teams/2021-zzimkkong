import { QueryFunction } from 'react-query';
import api from './api';

export const getValidateEmail: QueryFunction = ({ queryKey }) => {
  const [, email] = queryKey;

  return api.get(`/members/?email=${email}`);
};
