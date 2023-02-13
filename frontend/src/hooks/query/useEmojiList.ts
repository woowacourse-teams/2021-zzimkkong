import { AxiosError, AxiosResponse } from 'axios';
import { QueryKey, useQuery, UseQueryOptions, UseQueryResult } from 'react-query';
import { queryEmojiList } from 'api/join';
import { QueryEmojiListSuccess } from 'types/response';

const useEmojiList = <TData = AxiosResponse<QueryEmojiListSuccess>>(
  options?: UseQueryOptions<AxiosResponse<QueryEmojiListSuccess>, AxiosError, TData, [QueryKey]>
): UseQueryResult<TData, AxiosError> =>
  useQuery(['getEmojiList'], queryEmojiList, {
    ...options,
    refetchOnWindowFocus: false,
  });

export default useEmojiList;
