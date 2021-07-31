import { AxiosResponse } from 'axios';
import { QueryFunction } from 'react-query';
import { QueryManagerMapsSuccess } from 'types/response';
import api from './api';

interface PostMapParams {
  mapName: string;
  mapDrawing: string;
  mapImageSvg: string;
}

interface DeleteMapParams {
  mapId: number;
}

export const queryManagerMaps: QueryFunction<AxiosResponse<QueryManagerMapsSuccess>> = () =>
  api.get('/managers/maps');

export const postMap = ({
  mapName,
  mapDrawing,
  mapImageSvg,
}: PostMapParams): Promise<AxiosResponse<never>> =>
  api.post('/managers/maps', { mapName, mapDrawing, mapImageSvg });

export const deleteMap = ({ mapId }: DeleteMapParams): Promise<AxiosResponse<never>> =>
  api.delete(`/managers/maps/${mapId}`);
