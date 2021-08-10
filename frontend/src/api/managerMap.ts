import { AxiosResponse } from 'axios';
import { QueryFunction, QueryKey } from 'react-query';
import { QueryManagerMapSuccess } from 'types/response';
import api from './api';

export interface QueryManagerMapParams {
  mapId: number;
}

export const queryManagerMap: QueryFunction<
  AxiosResponse<QueryManagerMapSuccess>,
  [QueryKey, QueryManagerMapParams]
> = ({ queryKey }) => {
  const [, data] = queryKey;
  const { mapId } = data;

  return api.get(`/managers/maps/${mapId}`);
};
