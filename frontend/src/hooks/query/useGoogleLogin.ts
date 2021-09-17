import { AxiosError, AxiosResponse } from 'axios';
import { QueryKey, useQuery, UseQueryOptions, UseQueryResult } from 'react-query';
import { queryGoogleLogin, SocialLoginParams } from 'api/login';
import { SocialLoginFailure, LoginSuccess } from 'types/response';

const useGoogleLogin = <TData = AxiosResponse<LoginSuccess>>(
  { code }: SocialLoginParams,
  options?: UseQueryOptions<
    AxiosResponse<LoginSuccess>,
    AxiosError<SocialLoginFailure>,
    TData,
    [QueryKey, SocialLoginParams]
  >
): UseQueryResult<TData, AxiosError<SocialLoginFailure>> =>
  useQuery(['getGoogleLogin', { code }], queryGoogleLogin, {
    ...options,
    retry: false,
    refetchOnWindowFocus: false,
  });

export default useGoogleLogin;
