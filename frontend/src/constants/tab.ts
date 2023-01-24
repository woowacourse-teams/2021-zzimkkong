import { Tab } from 'components/TabLayout/TabLayout';
import PATH from './path';

export const TAB_LABEL: Record<string, Tab['label']> = {
  MANAGER: '관리자',
  GUEST: '예약자',
};

export const TAB_LIST: Tab[] = [
  { label: TAB_LABEL.MANAGER, path: PATH.MANAGER_MAP_LIST },
  { label: TAB_LABEL.GUEST, path: PATH.GUEST_MAIN },
];
