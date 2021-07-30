import { AxiosResponse } from 'axios';
import api from './api';

interface LoginParams {
  email: string;
  password: string;
}

export const postLogin = (loginData: LoginParams): Promise<AxiosResponse> => {
  return api.post('/login/token', loginData);
};

export const postTokenValidation = (): Promise<AxiosResponse> => {
  return api.post(`/members/token`);
};
