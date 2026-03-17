import { AxiosError, AxiosResponse } from 'axios';
import { QueryKey, useQuery, UseQueryOptions, UseQueryResult } from 'react-query';
import { querySharingSpaceId, QuerySharingSpaceIdParams, SharingSpaceIdData } from 'api/entry';
import { ErrorResponse } from 'types/response';

const useSharingSpaceId = <TData = AxiosResponse<SharingSpaceIdData>>(
  { mapId, spaceId }: QuerySharingSpaceIdParams,
  options?: UseQueryOptions<
    AxiosResponse<SharingSpaceIdData>,
    AxiosError<ErrorResponse>,
    TData,
    [QueryKey, QuerySharingSpaceIdParams]
  >
): UseQueryResult<TData, AxiosError<ErrorResponse>> =>
  useQuery(['getSharingSpaceId', { mapId, spaceId }], querySharingSpaceId, options);

export default useSharingSpaceId;
