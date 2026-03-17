import { AxiosResponse } from 'axios';
import { QueryFunction, QueryKey } from 'react-query';
import { Reservation, SpaceSetting, Group } from 'types/common';
import api from './api';

export interface SpaceEntryData {
  spaceId: number;
  spaceName: string;
  spaceColor: string;
  reservationEnable: boolean;
  settings: SpaceSetting[];
  allowedGroups: Group[];
  mapId: number;
  mapName: string;
  sharingMapId: string;
  todayReservations: Reservation[];
}

export interface QuerySpaceEntryParams {
  sharingSpaceId: string;
}

export interface QuerySharingSpaceIdParams {
  mapId: number;
  spaceId: number;
}

export interface SharingSpaceIdData {
  sharingSpaceId: string;
}

export const querySpaceEntry: QueryFunction<
  AxiosResponse<SpaceEntryData>,
  [QueryKey, QuerySpaceEntryParams]
> = ({ queryKey }) => {
  const [, data] = queryKey;
  const { sharingSpaceId } = data;

  return api.get(`/entry/spaces?sharingSpaceId=${sharingSpaceId}`);
};

export const querySharingSpaceId: QueryFunction<
  AxiosResponse<SharingSpaceIdData>,
  [QueryKey, QuerySharingSpaceIdParams]
> = ({ queryKey }) => {
  const [, data] = queryKey;
  const { mapId, spaceId } = data;

  return api.get(`/managers/maps/${mapId}/spaces/${spaceId}/sharing-id`);
};
