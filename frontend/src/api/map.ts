import { AxiosResponse } from 'axios';
import { QueryFunction, QueryKey } from 'react-query';
import { QueryMapsSuccess, QueryMapSuccess } from 'types/response';
import api from './api';

export interface QueryGuestMapParams {
  publicMapId: string;
}
interface PostMapParams {
  mapName: string;
  mapDrawing: string;
  mapImageSvg: string;
}

interface DeleteMapParams {
  mapId: number;
}

export const queryManagerMaps: QueryFunction<AxiosResponse<QueryMapsSuccess>> = () =>
  api.get('/managers/maps');

export const queryGuestMap: QueryFunction<
  AxiosResponse<QueryMapSuccess>,
  [QueryKey, QueryGuestMapParams]
> = ({ queryKey }) => {
  const [, data] = queryKey;
  const { publicMapId } = data;

  return api.get(`/guests/maps?publicMapId=${publicMapId}`);
};

export const postMap = ({
  mapName,
  mapDrawing,
  mapImageSvg,
}: PostMapParams): Promise<AxiosResponse<never>> =>
  api.post('/managers/maps', { mapName, mapDrawing, mapImageSvg });

export const deleteMap = ({ mapId }: DeleteMapParams): Promise<AxiosResponse<never>> =>
  api.delete(`/managers/maps/${mapId}`);
