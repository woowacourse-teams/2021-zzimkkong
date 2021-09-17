const GITHUB_OAUTH_KEY = (() => {
  if (process.env.NODE_ENV === 'development' && process.env.DEPLOY_ENV === 'development') {
    return process.env.GITHUB_OAUTH_KEY_LOCAL ?? '';
  }

  if (process.env.NODE_ENV === 'production' && process.env.DEPLOY_ENV === 'development') {
    return process.env.GITHUB_OAUTH_KEY_DEV ?? '';
  }

  if (process.env.NODE_ENV === 'production' && process.env.DEPLOY_ENV === 'production') {
    return process.env.GITHUB_OAUTH_KEY_PROD ?? '';
  }

  return '';
})();

const GOOGLE_OAUTH_KEY = process.env.GOOGLE_OAUTH_KEY ?? '';

const PATH = {
  MAIN: '/',
  MANAGER_LOGIN: '/login',
  MANAGER_JOIN: '/join',
  MANAGER_SOCIAL_JOIN: '/join/social',
  MANAGER_GITHUB_OAUTH_REDIRECT: '/login/oauth/github',
  MANAGER_GOOGLE_OAUTH_REDIRECT: '/login/oauth/google',
  MANAGER_MAIN: '/map',
  MANAGER_RESERVATION: '/reservation',
  MANAGER_RESERVATION_EDIT: '/reservation/edit',
  MANAGER_MAP_CREATE: '/map/create',
  MANAGER_MAP_EDIT: '/map/:mapId/edit',
  MANAGER_SPACE_EDIT: '/map/:mapId/space/edit',
  GUEST_MAP: '/guest/:sharingMapId',
  GUEST_RESERVATION: '/guest/:sharingMapId/reservation',
  GUEST_RESERVATION_EDIT: '/guest/:sharingMapId/reservation/edit',
  NOT_FOUND: '/not-found',
  GITHUB_LOGIN: `https://github.com/login/oauth/authorize?client_id=${GITHUB_OAUTH_KEY}&redirect_uri=http://localhost:3000/login/oauth/github`,
  GOOGLE_LOGIN:
    'https://accounts.google.com/o/oauth2/v2/auth?' +
    'scope=https://www.googleapis.com/auth/userinfo.email&' +
    'access_type=offline&' +
    'include_granted_scopes=true&' +
    'response_type=code&' +
    'state=state_parameter_passthrough_value&' +
    'redirect_uri=http://localhost:3000/login/oauth/google&' +
    `client_id=${GOOGLE_OAUTH_KEY}`,
};

export const HREF = {
  MANAGER_MAP_EDIT: (mapId: number): string => PATH.MANAGER_MAP_EDIT.replace(':mapId', `${mapId}`),
  MANAGER_SPACE_EDIT: (mapId: number): string =>
    PATH.MANAGER_SPACE_EDIT.replace(':mapId', `${mapId}`),
  GUEST_MAP: (sharingMapId: string): string =>
    PATH.GUEST_MAP.replace(':sharingMapId', `${sharingMapId}`),
  GUEST_RESERVATION: (sharingMapId: string): string =>
    PATH.GUEST_RESERVATION.replace(':sharingMapId', `${sharingMapId}`),
};

export default PATH;
