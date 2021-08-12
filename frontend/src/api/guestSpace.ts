import { AxiosResponse } from 'axios';
import { QueryFunction, QueryKey } from 'react-query';
import { QuerySpacesSuccess } from 'types/response';
import api from './api';

export interface QueryGuestSpacesParams {
  mapId: number;
}

export const queryGuestSpaces: QueryFunction<
  AxiosResponse<QuerySpacesSuccess>,
  [QueryKey, QueryGuestSpacesParams]
> = ({ queryKey }) => {
  const [, data] = queryKey;
  const { mapId } = data;

  return api.get(`/guests/maps/${mapId}/spaces`);
};
