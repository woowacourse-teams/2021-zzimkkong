import { QueryFunction } from 'react-query';
import api from './api';

export const postLogin: QueryFunction = ({ queryKey }) => {
  const [_key, loginData] = queryKey;

  return api.post('/login/token', loginData);
};
