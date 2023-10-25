import { AxiosResponse } from 'axios';
import { QueryFunction, QueryKey } from 'react-query';
import { QueryManagerMapSuccessV2 } from 'types/response-v2';
import apiV2 from './apiv2';

export interface QueryManagerMapParamsV2 {
  mapId: number;
}

interface PostMapParamsV2 {
  mapName: string;
  mapDrawing: string;
  thumbnail: string;
  slackUrl: string;
}

interface PutMapParamsV2 {
  mapId: number;
  mapName: string;
  mapDrawing: string;
  thumbnail: string;
  slackUrl: string;
}

export const queryManagerMapV2: QueryFunction<
  AxiosResponse<QueryManagerMapSuccessV2>,
  [QueryKey, QueryManagerMapParamsV2]
> = ({ queryKey }) => {
  const [, data] = queryKey;
  const { mapId } = data;

  return apiV2.get(`/api/maps/${mapId}`);
};

export const postMapV2 = ({
  mapName,
  mapDrawing,
  thumbnail,
  slackUrl,
}: PostMapParamsV2): Promise<AxiosResponse<never>> =>
  apiV2.post('/api/maps', { mapName, mapDrawing, thumbnail, slackUrl });

export const putMapV2 = ({
  mapId,
  mapName,
  mapDrawing,
  thumbnail,
  slackUrl,
}: PutMapParamsV2): Promise<AxiosResponse<never>> =>
  apiV2.put(`/api/maps/${mapId}`, { mapName, mapDrawing, thumbnail, slackUrl });
