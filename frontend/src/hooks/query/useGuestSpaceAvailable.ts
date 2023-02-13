import { AxiosError, AxiosResponse } from 'axios';
import { QueryKey, useQuery, UseQueryOptions } from 'react-query';
import { queryGuestSpaceAvailable, QueryGuestSpaceAvailableParams } from 'api/guestSpace';
import { ErrorResponse, QueryGuestSpaceAvailableSuccess } from 'types/response';

const useGuestSpaceAvailable = <TData = AxiosResponse<QueryGuestSpaceAvailableSuccess>>(
  params: QueryGuestSpaceAvailableParams,
  options?: UseQueryOptions<
    AxiosResponse<QueryGuestSpaceAvailableSuccess>,
    AxiosError<ErrorResponse>,
    TData,
    [QueryKey, QueryGuestSpaceAvailableParams]
  >
) => useQuery(['getGuestSpaceAvailable', params], () => queryGuestSpaceAvailable(params), options);

export default useGuestSpaceAvailable;
