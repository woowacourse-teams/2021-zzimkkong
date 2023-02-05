import { AxiosResponse } from 'axios';
import { QueryFunction, QueryKey } from 'react-query';
import { MapItem, Space } from 'types/common';
import { QueryGuestSpaceSuccess, QuerySpacesSuccess } from 'types/response';
import api from './api';

export interface QueryGuestSpacesParams {
  mapId: MapItem['mapId'];
}

export interface QueryGuestSpaceParams {
  mapId: MapItem['mapId'];
  spaceId: Space['id'];
}

export const queryGuestSpaces: QueryFunction<
  AxiosResponse<QuerySpacesSuccess>,
  [QueryKey, QueryGuestSpacesParams]
> = ({ queryKey }) => {
  const [, data] = queryKey;
  const { mapId } = data;

  return api.get(`/guests/maps/${mapId}/spaces`);
};

export const queryGusetSpace: QueryFunction<
  AxiosResponse<QueryGuestSpaceSuccess>,
  [QueryKey, QueryGuestSpaceParams]
> = ({ queryKey }) => {
  const [, data] = queryKey;
  const { mapId, spaceId } = data;

  return api.get(`/managers/maps/${mapId}/spaces/${spaceId}`);
};
