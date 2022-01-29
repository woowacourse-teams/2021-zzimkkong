import { AxiosError, AxiosResponse } from 'axios';
import { QueryKey, useQuery, UseQueryOptions, UseQueryResult } from 'react-query';
import { querySlackWebhookUrl, QuerySlackWebhookURLParams } from 'api/managerMap';
import { ErrorResponse, QuerySlackWebhookUrlSuccess } from 'types/response';

const useSlackWebhookUrl = <TData = AxiosResponse<QuerySlackWebhookUrlSuccess>>(
  { mapId }: QuerySlackWebhookURLParams,
  options?: UseQueryOptions<
    AxiosResponse<QuerySlackWebhookUrlSuccess>,
    AxiosError<ErrorResponse>,
    TData,
    [QueryKey, QuerySlackWebhookURLParams]
  >
): UseQueryResult<TData, AxiosError<ErrorResponse>> =>
  useQuery(['getSlackWebhookUrl', { mapId }], querySlackWebhookUrl, options);

export default useSlackWebhookUrl;
