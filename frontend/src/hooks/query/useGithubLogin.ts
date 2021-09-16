import { AxiosError, AxiosResponse } from 'axios';
import { QueryKey, useQuery, UseQueryOptions, UseQueryResult } from 'react-query';
import { queryGithubLogin, SocialLoginParams } from 'api/login';
import { LoginSuccess, SocialLoginFailure } from 'types/response';

const useGithubLogin = <TData = AxiosResponse<LoginSuccess>>(
  { code }: SocialLoginParams,
  options?: UseQueryOptions<
    AxiosResponse<LoginSuccess>,
    AxiosError<SocialLoginFailure>,
    TData,
    [QueryKey, SocialLoginParams]
  >
): UseQueryResult<TData, AxiosError<SocialLoginFailure>> =>
  useQuery(['getGithubLogin', { code }], queryGithubLogin, {
    ...options,
    retry: false,
    refetchOnWindowFocus: false,
  });

export default useGithubLogin;
