import { AxiosResponse } from 'axios';
import { QueryFunction, QueryKey } from 'react-query';
import { ReservationSettings } from 'types/common';
import { QueryPresetsSuccess } from 'types/response';
import api from './api';

export interface PostPresetParams {
  name: string;
  settingsRequest: ReservationSettings;
}

export interface DeletePresetParams {
  id: number;
}

export const queryPresets: QueryFunction<AxiosResponse<QueryPresetsSuccess>, [QueryKey]> = () =>
  api.get('/managers/presets');

export const postPreset = ({
  name,
  settingsRequest,
}: PostPresetParams): Promise<AxiosResponse<never>> =>
  api.post('/managers/presets', { name, settingsRequest });

export const deletePreset = ({ id }: DeletePresetParams): Promise<AxiosResponse<never>> =>
  api.delete(`/managers/presets/${id}`);
