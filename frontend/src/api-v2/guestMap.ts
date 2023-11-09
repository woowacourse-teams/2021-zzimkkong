import { AxiosResponse } from 'axios';
import { QueryFunction, QueryKey } from 'react-query';
import { QueryGuestMapSuccessV2 } from 'types/response-v2';
import apiV2 from './apiv2';

export interface QueryGuestMapParamsV2 {
  sharingMapId: string;
}

export const queryGuestMapV2: QueryFunction<
  AxiosResponse<QueryGuestMapSuccessV2>,
  [QueryKey, QueryGuestMapParamsV2]
> = ({ queryKey }) => {
  const [, data] = queryKey;
  const { sharingMapId } = data;

  return apiV2.get(`/api/maps/sharing?sharingMapId=${sharingMapId}`);
};
