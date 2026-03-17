import { AxiosError, AxiosResponse } from 'axios';
import { QueryKey, useQuery, UseQueryOptions, UseQueryResult } from 'react-query';
import { querySpaceEntry, QuerySpaceEntryParams, SpaceEntryData } from 'api/entry';
import { ErrorResponse } from 'types/response';

const useSpaceEntry = <TData = AxiosResponse<SpaceEntryData>>(
  { sharingSpaceId }: QuerySpaceEntryParams,
  options?: UseQueryOptions<
    AxiosResponse<SpaceEntryData>,
    AxiosError<ErrorResponse>,
    TData,
    [QueryKey, QuerySpaceEntryParams]
  >
): UseQueryResult<TData, AxiosError<ErrorResponse>> =>
  useQuery(['getSpaceEntry', { sharingSpaceId }], querySpaceEntry, options);

export default useSpaceEntry;
