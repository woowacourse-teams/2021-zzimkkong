import { AxiosResponse } from 'axios';
import { QueryFunction, QueryKey } from 'react-query';
import { QueryManagerMapSuccessV2 } from 'types/response-v2';
import apiV2 from './apiv2';

export interface QueryManagerMapParamsV2 {
  mapId: number;
}

export const queryManagerMapV2: QueryFunction<
  AxiosResponse<QueryManagerMapSuccessV2>,
  [QueryKey, QueryManagerMapParamsV2]
> = ({ queryKey }) => {
  const [, data] = queryKey;
  const { mapId } = data;

  return apiV2.get(`/api/maps/${mapId}`);
};
