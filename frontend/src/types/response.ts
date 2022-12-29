import {
  MapItem,
  Reservation,
  Space,
  SpaceReservation,
  ManagerSpaceAPI,
  Preset,
  Emoji,
} from './common';

export interface MapItemResponse extends Omit<MapItem, 'mapDrawing'> {
  mapDrawing: string;
}

export interface SpaceResponse extends Omit<Space, 'area'> {
  area: string;
}

export interface ErrorResponse {
  message?: string;
  field?: string;
}

export interface LoginSuccess {
  accessToken: string;
}

export interface SocialLoginFailure {
  message?: string;
  email?: string;
}

export interface QuerySocialEmailSuccess {
  email: string;
  oauthProvider: 'GITHUB' | 'GOOGLE';
}

export interface QueryEmojiListSuccess {
  emojis: Emoji[];
}

export type QueryGuestMapSuccess = MapItemResponse;

export type QueryManagerMapSuccess = MapItemResponse;

export interface QueryManagerMapsSuccess {
  maps: MapItemResponse[];
  organization: string;
}

export interface QueryManagerSpaceReservationsSuccess {
  reservations: Reservation[];
}
export interface QueryManagerMapReservationsSuccess {
  data: SpaceReservation[];
}

export interface QueryGuestReservationsSuccess {
  reservations: Reservation[];
}

export interface QuerySpacesSuccess {
  spaces: SpaceResponse[];
}

export interface QueryManagerSpaceSuccess {
  data: ManagerSpaceAPI;
}

export interface QueryManagerSpacesSuccess {
  spaces: ManagerSpaceAPI[];
}

export interface QueryPresetsSuccess {
  presets: Preset[];
}

export interface QuerySlackWebhookUrlSuccess {
  slackUrl: string;
}
