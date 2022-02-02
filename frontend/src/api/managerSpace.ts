import { AxiosResponse } from 'axios';
import { QueryFunction, QueryKey } from 'react-query';
import { Color, ReservationSettings } from 'types/common';
import { QueryManagerSpacesSuccess, QueryManagerSpaceSuccess } from 'types/response';
import api from './api';

export interface QueryManagerSpacesParams {
  mapId: number;
}

export interface QueryManagerSpaceParams {
  mapId: number;
  spaceId: number;
}

export interface PostManagerSpaceParams {
  mapId: number;
  space: {
    name: string;
    color: Color;
    description: string;
    area: string;
    settingsRequest: ReservationSettings;
    thumbnail: string;
  };
}

export interface PutManagerSpaceParams extends PostManagerSpaceParams {
  spaceId: number;
}

export interface DeleteManagerSpaceParams {
  mapId: number;
  spaceId: number;
  thumbnail: string;
}

export const queryManagerSpaces: QueryFunction<
  AxiosResponse<QueryManagerSpacesSuccess>,
  [QueryKey, QueryManagerSpacesParams]
> = ({ queryKey }) => {
  const [, data] = queryKey;
  const { mapId } = data;

  return api.get(`/managers/maps/${mapId}/spaces`);
};

export const queryManagerSpace: QueryFunction<
  AxiosResponse<QueryManagerSpaceSuccess>,
  [QueryKey, QueryManagerSpaceParams]
> = ({ queryKey }) => {
  const [, data] = queryKey;
  const { mapId, spaceId } = data;

  return api.get(`/managers/maps/${mapId}/spaces/${spaceId}`);
};

export const postManagerSpace = ({
  space,
  mapId,
}: PostManagerSpaceParams): Promise<AxiosResponse<never>> =>
  api.post(`/managers/maps/${mapId}/spaces`, space);

export const putManagerSpace = ({
  mapId,
  spaceId,
  space,
}: PutManagerSpaceParams): Promise<AxiosResponse<never>> =>
  api.put(`/managers/maps/${mapId}/spaces/${spaceId}`, space);

export const deleteManagerSpace = ({
  mapId,
  spaceId,
  thumbnail,
}: DeleteManagerSpaceParams): Promise<AxiosResponse<never>> =>
  api.delete(`/managers/maps/${mapId}/spaces/${spaceId}`, { data: { thumbnail } });
