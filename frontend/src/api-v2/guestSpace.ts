import { AxiosResponse } from 'axios';
import { QueryFunction, QueryKey } from 'react-query';
import { MapItem, SpaceV2 } from 'types/common';
import { QueryGuestSpaceSuccessV2, QuerySpacesSuccessV2 } from 'types/response-v2';
import apiV2 from './apiv2';

export interface QueryGuestSpacesParamsV2 {
  mapId: MapItem['mapId'];
}

export interface QueryGuestSpaceParamsV2 {
  mapId: MapItem['mapId'];
  spaceId: SpaceV2['id'];
}

export const queryGuestSpacesV2: QueryFunction<
  AxiosResponse<QuerySpacesSuccessV2>,
  [QueryKey, QueryGuestSpacesParamsV2]
> = ({ queryKey }) => {
  const [, data] = queryKey;
  const { mapId } = data;

  return apiV2.get(`/api/maps/${mapId}/spaces`);
};

export const queryGuestSpaceV2: QueryFunction<
  AxiosResponse<QueryGuestSpaceSuccessV2>,
  [QueryKey, QueryGuestSpaceParamsV2]
> = ({ queryKey }) => {
  const [, data] = queryKey;
  const { mapId, spaceId } = data;

  return apiV2.get(`/api/maps/${mapId}/spaces/${spaceId}`);
};
