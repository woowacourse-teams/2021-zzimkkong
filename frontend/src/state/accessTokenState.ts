import { atom } from 'recoil';
import { LOCAL_STORAGE_KEY } from '../constants/storage';
import { getLocalStorageItem } from '../utils/localStorage';
import { ACCESS_TOKEN_STATE_KEY } from './keys';

const accessTokenState = atom({
  key: ACCESS_TOKEN_STATE_KEY,
  default: getLocalStorageItem({ key: LOCAL_STORAGE_KEY.ACCESS_TOKEN, defaultValue: '' }) as string,
});

export default accessTokenState;
