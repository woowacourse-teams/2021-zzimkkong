import { QueryFunction } from 'react-query';
import api from './api';

export const getValidateEmail: QueryFunction = ({ queryKey }) => {
  const [_key, email] = queryKey;

  return api.get(`/members/?email=${email}`);
};
