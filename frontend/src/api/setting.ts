import { QS } from '@toss/utils';
import { MapItem, Space } from '../types/common';
import { QuerySettingSummarySuccess } from '../types/response';
import api from './api';

export interface QuerySettingSummaryParams {
  mapId: MapItem['mapId'];
  spaceId: Space['id'];
  selectedDateTime: string | null;
  settingViewType: string | null;
}

export const querySettingSummary = ({
  mapId,
  spaceId,
  selectedDateTime,
  settingViewType,
}: QuerySettingSummaryParams) => {
  return api.get<QuerySettingSummarySuccess>(
    `/maps/${mapId}/spaces/${spaceId}/settings/summary${QS.create({
      selectedDateTime: selectedDateTime,
      settingViewType: settingViewType,
    })}`
  );
};
