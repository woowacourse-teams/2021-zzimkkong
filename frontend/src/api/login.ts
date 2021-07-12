import api from './api';

interface LoginParams {
  email: string;
  password: string;
}

export const postLogin = (loginData: LoginParams) => {
  return api.post('/login/token', loginData);
};
