import { AxiosResponse } from 'axios';
import { QueryFunction, QueryKey } from 'react-query';
import { QuerySpacesSuccess } from 'types/response';
import api from './api';

export interface QuerySpacesParams {
  mapId: number;
  date: string;
}

export const querySpaces: QueryFunction<
  AxiosResponse<QuerySpacesSuccess>,
  [QueryKey, QuerySpacesParams]
> = ({ queryKey }) => {
  const [, data] = queryKey;
  const { mapId, date } = data;

  return api.get(`/maps/${mapId}/reservations?date=${date}`);
};
