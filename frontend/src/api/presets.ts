import { AxiosResponse } from 'axios';
import { QueryFunction, QueryKey } from 'react-query';
import { ReservationSettings } from 'types/common';
import { QueryPresetsSuccess } from 'types/response';
import api from './api';

export interface PostPresetParams {
  name: string;
  settingsRequest: ReservationSettings;
}

export const queryPresets: QueryFunction<AxiosResponse<QueryPresetsSuccess>, [QueryKey]> = () =>
  api.get(`/members/presets`);

export const postPreset = ({
  name,
  settingsRequest,
}: PostPresetParams): Promise<AxiosResponse<never>> =>
  api.post(`/members/presets`, { name, settingsRequest });
