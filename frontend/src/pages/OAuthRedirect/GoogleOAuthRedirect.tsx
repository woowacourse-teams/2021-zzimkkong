import { AxiosError, AxiosResponse } from 'axios';
import { useContext } from 'react';
import { useHistory } from 'react-router';
import MESSAGE from 'constants/message';
import PATH from 'constants/path';
import useGoogleLogin from 'hooks/query/useGoogleLogin';
import useQueryString from 'hooks/useQueryString';
import { AccessTokenContext } from 'providers/AccessTokenProvider';
import { LoginSuccess, SocialLoginFailure } from 'types/response';

const GoogleOAuthRedirect = (): JSX.Element => {
  const history = useHistory();
  const { setAccessToken } = useContext(AccessTokenContext);

  const query = useQueryString();
  const code = query.get('code') ?? '';

  useGoogleLogin(
    { code },
    {
      onSuccess: (response: AxiosResponse<LoginSuccess>) => {
        const { accessToken } = response.data;

        setAccessToken(accessToken);

        history.replace(PATH.MANAGER_MAP_DETAIL);
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
