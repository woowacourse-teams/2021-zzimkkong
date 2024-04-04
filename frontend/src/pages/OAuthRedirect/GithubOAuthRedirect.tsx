import { AxiosError, AxiosResponse } from 'axios';
import { useContext } from 'react';
import { useHistory } from 'react-router';
import MESSAGE from 'constants/message';
import PATH from 'constants/path';
import { LOCAL_STORAGE_KEY } from 'constants/storage';
import useGithubLogin from 'hooks/query/useGithubLogin';
import useQueryString from 'hooks/useQueryString';
import { AccessTokenContext } from 'providers/AccessTokenProvider';
import { LoginSuccess, SocialLoginFailure } from 'types/response';
import { getLocalStorageItem, removeLocalStorageItem } from 'utils/localStorage';

const GithubOAuthRedirect = (): JSX.Element => {
  const history = useHistory();
  const { setAccessToken } = useContext(AccessTokenContext);

  const query = useQueryString();
  const code = query.get('code') ?? '';

  useGithubLogin(
    { code },
    {
      onSuccess: (response: AxiosResponse<LoginSuccess>) => {
        const { accessToken } = response.data;

        setAccessToken(accessToken);

        const afterLoginPath = getLocalStorageItem({
          key: LOCAL_STORAGE_KEY.AFTER_LOGIN_PATH,
          defaultValue: PATH.GUEST_MAIN,
        });

        history.replace(afterLoginPath);
      },

      onError: (error: AxiosError<SocialLoginFailure>) => {
        if (error.response?.status === 404) {
          history.push({
            pathname: PATH.MANAGER_SOCIAL_JOIN,
            state: {
              oauthProvider: 'GITHUB',
              email: error.response?.data?.email,
            },
          });

          return;
        }

        alert(error.response?.data.message ?? MESSAGE.LOGIN.UNEXPECTED_ERROR);

        const afterLoginPath = getLocalStorageItem({
          key: LOCAL_STORAGE_KEY.AFTER_LOGIN_PATH,
          defaultValue: PATH.LOGIN,
        });

        history.replace(afterLoginPath);
      },

      onSettled: () => {
        removeLocalStorageItem({ key: LOCAL_STORAGE_KEY.AFTER_LOGIN_PATH });
      },
    }
  );

  return <div />;
};

export default GithubOAuthRedirect;
