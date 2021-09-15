import { AxiosError, AxiosResponse } from 'axios';
import { QueryKey, useQuery, UseQueryOptions, UseQueryResult } from 'react-query';
import { queryPresets } from 'api/presets';
import { ErrorResponse, QueryPresetsSuccess } from 'types/response';

const usePresets = <TData = AxiosResponse<QueryPresetsSuccess>>(
  options?: UseQueryOptions<
    AxiosResponse<QueryPresetsSuccess>,
    AxiosError<ErrorResponse>,
    TData,
    [QueryKey]
  >
): UseQueryResult<TData, AxiosError<ErrorResponse>> =>
  useQuery(['getPresets'], queryPresets, options);

export default usePresets;
