import { AxiosResponse } from 'axios';
import { QueryFunction } from 'react-query';
import { QueryManagerMapsSuccess } from 'types/response';
import api from './api';

interface PostMapParams {
  mapName: string;
  mapDrawing: string;
  mapImageSvg: string;
}

export const postMap = ({
  mapName,
  mapDrawing,
  mapImageSvg,
}: PostMapParams): Promise<AxiosResponse<never>> =>
  api.post('/managers/maps', { mapName, mapDrawing, mapImageSvg });

export const queryManagerMaps: QueryFunction<AxiosResponse<QueryManagerMapsSuccess>> = () =>
  api.get('/managers/maps');