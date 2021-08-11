import { AxiosResponse } from 'axios';
import { QueryFunction, QueryKey } from 'react-query';
import { QueryManagerSpacesSuccess } from 'types/response';
import api from './api';

export interface QueryManagerSpacesParams {
  mapId: number;
}

export const queryManagerSpaces: QueryFunction<
  AxiosResponse<QueryManagerSpacesSuccess>,
  [QueryKey, QueryManagerSpacesParams]
> = ({ queryKey }) => {
  const [, data] = queryKey;
  const { mapId } = data;

  return api.get(`/managers/maps/${mapId}/spaces`);
};
