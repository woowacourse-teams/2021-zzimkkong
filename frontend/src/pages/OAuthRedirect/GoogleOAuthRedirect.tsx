import { AxiosError, AxiosResponse } from 'axios';
import { useHistory } from 'react-router';
import { useSetRecoilState } from 'recoil';
import MESSAGE from 'constants/message';
import PATH from 'constants/path';
import { LOCAL_STORAGE_KEY } from 'constants/storage';
import useGoogleLogin from 'hooks/query/useGoogleLogin';
import useQueryString from 'hooks/useQueryString';
import accessTokenState from 'state/accessTokenState';
import { SocialLoginFailure, LoginSuccess } from 'types/response';
import { setLocalStorageItem } from 'utils/localStorage';

const GoogleOAuthRedirect = (): JSX.Element => {
  const history = useHistory();
  const setAccessToken = useSetRecoilState(accessTokenState);

  const query = useQueryString();
  const code = query.get('code') ?? '';

  useGoogleLogin(
    { code },
    {
      onSuccess: (response: AxiosResponse<LoginSuccess>) => {
        const { accessToken } = response.data;

        setAccessToken(accessToken);
        setLocalStorageItem({ key: LOCAL_STORAGE_KEY.ACCESS_TOKEN, item: accessToken });

        history.replace(PATH.MANAGER_MAIN);
      },

      onError: (error: AxiosError<SocialLoginFailure>) => {
        if (error.response?.status === 404) {
          history.push({
            pathname: PATH.MANAGER_SOCIAL_JOIN,
            state: {
              oauthProvider: 'GOOGLE',
              email: error.response?.data?.email,
            },
          });

          return;
        }

        alert(error.response?.data.message ?? MESSAGE.LOGIN.UNEXPECTED_ERROR);

        history.replace(PATH.MANAGER_LOGIN);
      },
    }
  );

  return <div />;
};

export default GoogleOAuthRedirect;
