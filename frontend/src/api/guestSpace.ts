import { QS } from '@toss/utils';
import { AxiosResponse } from 'axios';
import { QueryFunction, QueryKey } from 'react-query';
import { MapItem, Space } from 'types/common';
import {
  QueryGuestSpaceAvailableSuccess,
  QueryGuestSpaceSuccess,
  QuerySpacesSuccess,
} from 'types/response';
import api from './api';

export interface QueryGuestSpacesParams {
  mapId: MapItem['mapId'];
}

export interface QueryGuestSpaceParams {
  mapId: MapItem['mapId'];
  spaceId: Space['id'];
}

export interface QueryGuestSpaceAvailableParams {
  mapId: MapItem['mapId'];
  startDateTime: string;
  endDateTime: string;
}

export const queryGuestSpaces: QueryFunction<
  AxiosResponse<QuerySpacesSuccess>,
  [QueryKey, QueryGuestSpacesParams]
> = ({ queryKey }) => {
  const [, data] = queryKey;
  const { mapId } = data;

  return api.get(`/guests/maps/${mapId}/spaces`);
};

export const queryGuestSpace: QueryFunction<
  AxiosResponse<QueryGuestSpaceSuccess>,
  [QueryKey, QueryGuestSpaceParams]
> = ({ queryKey }) => {
  const [, data] = queryKey;
  const { mapId, spaceId } = data;

  return api.get(`/guests/maps/${mapId}/spaces/${spaceId}`);
};

export const queryGuestSpaceAvailable = ({
  mapId,
  startDateTime,
  endDateTime,
}: QueryGuestSpaceAvailableParams) => {
  return api.get<QueryGuestSpaceAvailableSuccess>(
    `/guests/maps/${mapId}/spaces/availability${QS.create({
      startDateTime,
      endDateTime,
    })}`
  );
};
