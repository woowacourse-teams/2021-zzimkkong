import { AxiosError, AxiosResponse } from 'axios';
import { QueryKey, useQuery, UseQueryOptions, UseQueryResult } from 'react-query';
import { ErrorResponse, QuerySettingSummarySuccess } from 'types/response';
import { querySettingSummary, QuerySettingSummaryParams } from '../../api/setting';

const useSettingSummary = <TData = AxiosResponse<QuerySettingSummarySuccess>>(
  params: QuerySettingSummaryParams,
  options?: UseQueryOptions<
    AxiosResponse<QuerySettingSummarySuccess>,
    AxiosError<ErrorResponse>,
    TData,
    [QueryKey, QuerySettingSummaryParams]
  >
): UseQueryResult<TData, AxiosError<ErrorResponse>> =>
  useQuery(['getSettingSummary', params], () => querySettingSummary(params), options);

export default useSettingSummary;
