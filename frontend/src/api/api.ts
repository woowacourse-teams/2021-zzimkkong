import axios from 'axios';

const api = axios.create({
  baseURL: 'https://zzimkkong-proxy.o-r.kr/api',
  headers: {
    'Content-type': 'application/json',
  },
});

export default api;
