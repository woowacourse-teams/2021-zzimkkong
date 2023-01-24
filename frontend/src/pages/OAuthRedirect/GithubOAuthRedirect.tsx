import { AxiosError, AxiosResponse } from 'axios';
import { useContext } from 'react';
import { useHistory } from 'react-router';
import MESSAGE from 'constants/message';
import PATH from 'constants/path';
import useGithubLogin from 'hooks/query/useGithubLogin';
import useQueryString from 'hooks/useQueryString';
import { AccessTokenContext } from 'providers/AccessTokenProvider';
import { LoginSuccess, SocialLoginFailure } from 'types/response';

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

        history.replace(PATH.GUEST_MAIN);
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

        history.replace(PATH.LOGIN);
      },
    }
  );

  return <div />;
};

export default GithubOAuthRedirect;
