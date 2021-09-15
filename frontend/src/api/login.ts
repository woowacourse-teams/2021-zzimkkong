import { AxiosResponse } from 'axios';
import api from './api';

interface LoginParams {
  email: string;
  password: string;
}

export const postLogin = (loginData: LoginParams): Promise<AxiosResponse> => {
  return api.post('/managers/login/token', loginData);
};
