import { TOKEN_KEY } from './../constants/storage';
import { getLocalStorageItem } from './../utils/localStorage';
import { atom } from 'recoil';

const tokenState = atom({
  key: 'tokenState',
  default: getLocalStorageItem({ key: TOKEN_KEY, defaultValue: '' }),
});

export default tokenState;
