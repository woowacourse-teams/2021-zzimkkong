const PATH = {
  MAIN: '/',
  MANAGER_LOGIN: '/login',
  MANAGER_JOIN: '/join',
  MANAGER_MAIN: '/map',
  MANAGER_RESERVATION_EDIT: '/reservation/edit',
  MANAGER_MAP_CREATE: '/map/create',
  MANAGER_MAP_EDIT: '/map/:mapId/edit',
  MANAGER_SPACE_EDIT: '/map/:mapId/space/edit',
  GUEST_MAP: '/guest/:sharingMapId',
  GUEST_RESERVATION: '/guest/:sharingMapId/reservation',
  GUEST_RESERVATION_EDIT: '/guest/:sharingMapId/reservation/edit',
  NOT_FOUND: ['*', '/not-found'],
};

export const HREF = {
  MANAGER_MAP_EDIT: (mapId: number): string => PATH.MANAGER_MAP_EDIT.replace(':mapId', `${mapId}`),
  MANAGER_SPACE_EDIT: (mapId: number): string =>
    PATH.MANAGER_SPACE_EDIT.replace(':mapId', `${mapId}`),
  GUEST_MAP: (sharingMapId: string): string =>
    PATH.GUEST_MAP.replace(':sharingMapId', `${sharingMapId}`),
};

export default PATH;
