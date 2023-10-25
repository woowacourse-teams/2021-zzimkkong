import { MapItem } from './common';

export interface MapItemResponseV2
  extends Omit<MapItem, 'mapDrawing' | 'sharingMapId' | 'notice' | 'managerEmail'> {
  mapDrawing: string;
  slackUrl: string;
}

export type QueryManagerMapSuccessV2 = MapItemResponseV2;
