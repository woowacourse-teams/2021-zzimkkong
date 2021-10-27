import { ReactNode, useState, createContext } from 'react';
import { LOCAL_STORAGE_KEY } from 'constants/storage';
import { removeLocalStorageItem, setLocalStorageItem } from 'utils/localStorage';

interface Props {
  children: ReactNode;
}

interface AccessTokenContextValue {
  accessToken: string | null;
  setToken: (token: string) => void;
  resetToken: () => void;
}

const AccessTokenContext = createContext<AccessTokenContextValue>({
  accessToken: null,
  setToken: (token: string) => undefined,
  resetToken: () => undefined,
});

const AccessTokenProvider = ({ children }: Props): JSX.Element => {
  const [accessToken, setAccessToken] = useState<string | null>(null);

  const setToken = (token: string) => {
    setAccessToken(token);
    setLocalStorageItem({ key: LOCAL_STORAGE_KEY.ACCESS_TOKEN, item: token });
  };

  const resetToken = () => {
    setAccessToken(null);
    removeLocalStorageItem({ key: LOCAL_STORAGE_KEY.ACCESS_TOKEN });
  };

  return (
    <AccessTokenContext.Provider value={{ accessToken, setToken, resetToken }}>
      {children}
    </AccessTokenContext.Provider>
  );
};

export default AccessTokenProvider;
