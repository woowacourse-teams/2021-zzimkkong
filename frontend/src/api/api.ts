import axios from 'axios';
import { LOCAL_STORAGE_KEY } from 'constants/storage';
import { getLocalStorageItem } from 'utils/localStorage';

const api = axios.create({
  baseURL: 'https://zzimkkong-proxy.o-r.kr/api',
  headers: {
    'Content-type': 'application/json',
  },
});

api.interceptors.request.use((config) => {
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
});

export default api;
