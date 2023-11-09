import axios, { AxiosError } from 'axios';
import { history } from 'App';
import PATH from 'constants/path';
import { LOCAL_STORAGE_KEY } from 'constants/storage';
import { ErrorResponse } from 'types/response';
import { getLocalStorageItem, removeLocalStorageItem } from 'utils/localStorage';

const apiV2 = axios.create({
  baseURL: 'http://192.168.5.254:8080',
  // baseURL: 'http://192.168.2.160:8080/',
  headers: {
    'Content-type': 'application/json',
  },
});

apiV2.interceptors.request.use(
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

apiV2.interceptors.response.use(
  (response) => {
    return response;
  },

  (error: AxiosError<ErrorResponse>) => {
    if (error?.response?.status === 401) {
      removeLocalStorageItem({ key: LOCAL_STORAGE_KEY.ACCESS_TOKEN });

      history.push(PATH.LOGIN);
    }

    return Promise.reject(error);
  }
);

export default apiV2;
