import React, { ReactNode } from 'react';
import PATH from './path';

const GuestMap = React.lazy(() => import('pages/GuestMap/GuestMap'));
const GuestReservation = React.lazy(() => import('pages/GuestReservation/GuestReservation'));
const Main = React.lazy(() => import('pages/Main/Main'));
const ManagerJoin = React.lazy(() => import('pages/ManagerJoin/ManagerJoin'));
const ManagerSocialJoin = React.lazy(() => import('pages/ManagerSocialJoin/ManagerSocialJoin'));
const ManagerLogin = React.lazy(() => import('pages/ManagerLogin/ManagerLogin'));
const ManagerMain = React.lazy(() => import('pages/ManagerMain/ManagerMain'));
const ManagerMapEditor = React.lazy(() => import('pages/ManagerMapEditor/ManagerMapEditor'));
const ManagerReservation = React.lazy(() => import('pages/ManagerReservation/ManagerReservation'));
const ManagerSpaceEditor = React.lazy(() => import('pages/ManagerSpaceEditor/ManagerSpaceEditor'));
const GithubOAuthRedirect = React.lazy(() => import('pages/OAuthRedirect/GithubOAuthRedirect'));
const GoogleOAuthRedirect = React.lazy(() => import('pages/OAuthRedirect/GoogleOAuthRedirect'));

interface Route {
  path: string;
  component: ReactNode;
}

interface PrivateRoute extends Route {
  redirectPath: string;
}

export const PUBLIC_ROUTES: Route[] = [
  {
    path: PATH.MAIN,
    component: <Main />,
  },
  {
    path: PATH.MANAGER_LOGIN,
    component: <ManagerLogin />,
  },
  {
    path: PATH.MANAGER_JOIN,
    component: <ManagerJoin />,
  },
  {
    path: PATH.MANAGER_SOCIAL_JOIN,
    component: <ManagerSocialJoin />,
  },
  {
    path: PATH.MANAGER_GITHUB_OAUTH_REDIRECT,
    component: <GithubOAuthRedirect />,
  },
  {
    path: PATH.MANAGER_GOOGLE_OAUTH_REDIRECT,
    component: <GoogleOAuthRedirect />,
  },
  {
    path: PATH.GUEST_MAP,
    component: <GuestMap />,
  },
  {
    path: PATH.GUEST_RESERVATION,
    component: <GuestReservation />,
  },
  {
    path: PATH.GUEST_RESERVATION_EDIT,
    component: <GuestReservation />,
  },
];

export const PRIVATE_ROUTES: PrivateRoute[] = [
  {
    path: PATH.MANAGER_MAIN,
    component: <ManagerMain />,
    redirectPath: PATH.MANAGER_LOGIN,
  },
  {
    path: PATH.MANAGER_RESERVATION,
    component: <ManagerReservation />,
    redirectPath: PATH.MANAGER_LOGIN,
  },
  {
    path: PATH.MANAGER_RESERVATION_EDIT,
    component: <ManagerReservation />,
    redirectPath: PATH.MANAGER_LOGIN,
  },
  {
    path: PATH.MANAGER_MAP_CREATE,
    component: <ManagerMapEditor />,
    redirectPath: PATH.MANAGER_LOGIN,
  },
  {
    path: PATH.MANAGER_MAP_EDIT,
    component: <ManagerMapEditor />,
    redirectPath: PATH.MANAGER_LOGIN,
  },
  {
    path: PATH.MANAGER_SPACE_EDIT,
    component: <ManagerSpaceEditor />,
    redirectPath: PATH.MANAGER_LOGIN,
  },
];
