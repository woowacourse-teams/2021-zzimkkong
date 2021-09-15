import { AxiosResponse } from 'axios';
import { QueryFunction, QueryKey } from 'react-query';
import THROW_ERROR from 'constants/throwError';
import { QueryManagerSpacesSuccess } from 'types/response';
import api from './api';

export interface QueryManagerSpacesParams {
  mapId: number | null;
}

export const queryManagerSpaces: QueryFunction<
  AxiosResponse<QueryManagerSpacesSuccess>,
  [QueryKey, QueryManagerSpacesParams]
> = ({ queryKey }) => {
  const [, data] = queryKey;
  const { mapId } = data;

  if (!mapId) {
    throw new Error(THROW_ERROR.INVALID_MAP_ID);
  }

  return api.get(`/managers/maps/${mapId}/spaces`);
};
