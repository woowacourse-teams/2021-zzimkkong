import { AxiosResponse } from 'axios';
import { QueryFunction } from 'react-query';
import { QueryManagerMapsSuccessV2 } from 'types/response-v2';
import apiV2 from './apiv2';

interface DeleteMapParamsV2 {
  mapId: number;
}

export const queryManagerMapsV2: QueryFunction<AxiosResponse<QueryManagerMapsSuccessV2>> = () =>
  apiV2.get('/api/maps');

export const deleteMapV2 = ({ mapId }: DeleteMapParamsV2): Promise<AxiosResponse<never>> =>
  apiV2.delete(`/api/maps/${mapId}`);
