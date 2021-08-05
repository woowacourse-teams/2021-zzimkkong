const PATH = {
  MANAGER_MAIN: '/',
  MANAGER_LOGIN: '/login',
  MANAGER_JOIN: '/join',
  MANAGER_MAP_CREATE: '/map/create',
  MANAGER_MAP_EDIT: '/map/:mapId/edit',
  MANAGER_SPACE_EDIT: '/map/:mapId/space/edit',
  GUEST_MAIN: '/guest',
  GUEST_MAP: '/guest/:publicMapId',
  GUEST_RESERVATION: '/guest/reservation',
  GUEST_RESERVATION_EDIT: '/guest/reservation/edit',
  NOT_FOUND: ['*', '/not-found'],
};

export const HREF = {
  MANAGER_MAP_EDIT: (mapId: number): string => PATH.MANAGER_MAP_EDIT.replace(':mapId', `${mapId}`),
  MANAGER_SPACE_EDIT: (mapId: number): string =>
    PATH.MANAGER_SPACE_EDIT.replace(':mapId', `${mapId}`),
};

export default PATH;
