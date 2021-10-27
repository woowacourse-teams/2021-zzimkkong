import { ReactNode, useState, createContext } from 'react';
import { LOCAL_STORAGE_KEY } from 'constants/storage';
import {
  getLocalStorageItem,
  removeLocalStorageItem,
  setLocalStorageItem,
} from 'utils/localStorage';

interface Props {
  children: ReactNode;
}

interface AccessTokenContextValue {
  accessToken: string | null;
  setAccessToken: (token: string) => void;
  resetAccessToken: () => void;
}

export const AccessTokenContext = createContext<AccessTokenContextValue>({
  accessToken: null,
  setAccessToken: (token: string) => undefined,
  resetAccessToken: () => undefined,
});

const AccessTokenProvider = ({ children }: Props): JSX.Element => {
  const [token, setToken] = useState<AccessTokenContextValue['accessToken']>(
    getLocalStorageItem({ key: LOCAL_STORAGE_KEY.ACCESS_TOKEN, defaultValue: null }) as
      | AccessTokenContextValue['accessToken']
  );

  const setAccessToken = (accessToken: string) => {
    console.log('call setAccessToken', accessToken);

    setToken(accessToken);
    setLocalStorageItem({ key: LOCAL_STORAGE_KEY.ACCESS_TOKEN, item: accessToken });
  };

  const resetAccessToken = () => {
    console.log('call resetAccessToken');

    setToken(null);
    removeLocalStorageItem({ key: LOCAL_STORAGE_KEY.ACCESS_TOKEN });
  };

  return (
    <AccessTokenContext.Provider value={{ accessToken: token, setAccessToken, resetAccessToken }}>
      {children}
    </AccessTokenContext.Provider>
  );
};

export default AccessTokenProvider;
