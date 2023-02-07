import { TabItem } from 'components/TabLayout/Tab';
import PATH from './path';

export const TAB_LABEL: Record<string, TabItem> = {
  MANAGER: '관리자',
  GUEST: '예약자',
};

export const TAB_LIST: TabItem[] = [TAB_LABEL.MANAGER, TAB_LABEL.GUEST];

export const TAB_PATH_FOR_LABEL: Record<TabItem, string> = {
  [TAB_LABEL.MANAGER]: PATH.MANAGER_MAP_LIST,
  [TAB_LABEL.GUEST]: PATH.GUEST_MAIN,
};
