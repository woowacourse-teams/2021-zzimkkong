import axios, { AxiosError } from 'axios';
import PATH from 'constants/path';
import { LOCAL_STORAGE_KEY } from 'constants/storage';
import { ErrorResponse } from 'types/response';
import { getLocalStorageItem } from 'utils/localStorage';

const api = axios.create({
  baseURL: 'https://zzimkkong-proxy.o-r.kr/api',
  headers: {
    'Content-type': 'application/json',
  },
});

api.interceptors.request.use(
  (config) => {
    const token = getLocalStorageItem({
      key: LOCAL_STORAGE_KEY.ACCESS_TOKEN,
      defaultValue: '',
    });

    if (typeof token !== 'string' || !token) return config;

    config.headers = {
      'Content-type': 'application/json',
      Authorization: `Bearer ${token}`,
    };

    return config;
  },

  (error) => {
    return Promise.reject(error);
  }
);

api.interceptors.response.use(
  (response) => {
    return response;
  },

  (error: AxiosError<ErrorResponse>) => {
    if (error?.response?.status === 401) {
      localStorage.removeItem(LOCAL_STORAGE_KEY.ACCESS_TOKEN);
      window.history.pushState(null, '', PATH.MANAGER_LOGIN);
    }

    return Promise.reject(error);
  }
);

export default api;
