import { AxiosResponse } from 'axios';
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
