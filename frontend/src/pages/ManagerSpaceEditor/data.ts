import PALETTE from 'constants/palette';
import { Area } from 'types/common';
import { SpaceEditorMode } from 'types/editor';
import { formatDate, formatTimeWithSecond } from 'utils/datetime';

export interface SpaceFormValue {
  name: string;
  color: string;
  area: Area | null;
  reservationEnable: boolean;
  description?: string;
  settings: {
    settingStartTime: string;
    settingEndTime: string;
    reservationTimeUnit: number;
    reservationMinimumTimeUnit: number;
    reservationMaximumTimeUnit: number;
    enabledDayOfWeek: {
      monday: boolean;
      tuesday: boolean;
      wednesday: boolean;
      thursday: boolean;
      friday: boolean;
      saturday: boolean;
      sunday: boolean;
    };
  }[];
}

const today = formatDate(new Date());

export const initialEnabledDayOfWeek: SpaceFormValue['settings'][number]['enabledDayOfWeek'] = {
  monday: true,
  tuesday: true,
  wednesday: true,
  thursday: true,
  friday: true,
  saturday: true,
  sunday: true,
};

export const initialSpaceFormValue: Omit<SpaceFormValue, 'area'> = {
  reservationEnable: true,
  name: '',
  color: PALETTE.RED[500],
  settings: [
    {
      settingStartTime: formatTimeWithSecond(new Date(`${today}T07:00:00`)),
      settingEndTime: formatTimeWithSecond(new Date(`${today}T23:00:00`)),
      reservationTimeUnit: 10,
      reservationMinimumTimeUnit: 10,
      reservationMaximumTimeUnit: 120,
      enabledDayOfWeek: initialEnabledDayOfWeek,
    },
  ],
};

export const colorSelectOptions = [
  PALETTE.RED[500],
  PALETTE.ORANGE[500],
  PALETTE.YELLOW[500],
  PALETTE.GREEN[500],
  PALETTE.BLUE[300],
  PALETTE.BLUE[900],
  PALETTE.PURPLE[500],
];

export const timeUnits = ['5', '10', '30', '60'];

export const drawingModes = [SpaceEditorMode.Rect, SpaceEditorMode.Polygon];
