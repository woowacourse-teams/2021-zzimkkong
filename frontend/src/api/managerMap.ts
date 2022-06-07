import { AxiosResponse } from 'axios';
import { QueryFunction, QueryKey } from 'react-query';
import {
  QueryManagerMapSuccess,
  QueryManagerMapsSuccess,
  QuerySlackWebhookUrlSuccess,
} from 'types/response';
import api from './api';

export interface QueryManagerMapParams {
  mapId: number;
}

interface PostMapParams {
  mapName: string;
  mapDrawing: string;
  thumbnail: string;
}

interface PutMapParams {
  mapId: number;
  mapName: string;
  mapDrawing: string;
  thumbnail: string;
}

interface DeleteMapParams {
  mapId: number;
}

export interface QuerySlackWebhookURLParams {
  mapId: number;
}

interface PostSlackWebhookURLParams {
  mapId: number;
  slackUrl: string;
}

interface PostNotice {
  mapId: number;
  notice: string;
}

export const queryManagerMaps: QueryFunction<AxiosResponse<QueryManagerMapsSuccess>> = () =>
  api.get('/managers/maps');

export const queryManagerMap: QueryFunction<
  AxiosResponse<QueryManagerMapSuccess>,
  [QueryKey, QueryManagerMapParams]
> = ({ queryKey }) => {
  const [, data] = queryKey;
  const { mapId } = data;

  return api.get(`/managers/maps/${mapId}`);
};

export const postMap = ({
  mapName,
  mapDrawing,
  thumbnail,
}: PostMapParams): Promise<AxiosResponse<never>> =>
  api.post('/managers/maps', { mapName, mapDrawing, thumbnail });

export const putMap = ({
  mapId,
  mapName,
  mapDrawing,
  thumbnail,
}: PutMapParams): Promise<AxiosResponse<never>> =>
  api.put(`/managers/maps/${mapId}`, { mapName, mapDrawing, thumbnail });

export const deleteMap = ({ mapId }: DeleteMapParams): Promise<AxiosResponse<never>> =>
  api.delete(`/managers/maps/${mapId}`);

export const querySlackWebhookUrl: QueryFunction<
  AxiosResponse<QuerySlackWebhookUrlSuccess>,
  [QueryKey, QuerySlackWebhookURLParams]
> = ({ queryKey }) => {
  const [, data] = queryKey;
  const { mapId } = data;

  return api.get(`/managers/maps/${mapId}/slack`);
};

export const postSlackWebhookUrl = ({
  mapId,
  slackUrl,
}: PostSlackWebhookURLParams): Promise<AxiosResponse<never>> =>
  api.post(`/managers/maps/${mapId}/slack`, { slackUrl });

export const postNotice = ({ mapId, notice }: PostNotice): Promise<AxiosResponse<never>> =>
  api.post(`/managers/maps/${mapId}/notice`, { notice });
