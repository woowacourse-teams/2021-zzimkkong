import React, { ReactNode } from 'react';
import GuestMain from 'pages/GuestMain/GuestMain';
import GuestMapContainer from 'pages/GuestMap/GuestMapContainer';
import ManagerMapList from 'pages/ManagerMapList/ManagerMapList';
import PATH from './path';

const GuestMap = React.lazy(() => import('pages/GuestMap/GuestMap'));
const GuestReservation = React.lazy(() => import('pages/GuestReservation/GuestReservation'));
const GuestReservationSuccess = React.lazy(
  () => import('pages/GuestReservation/GuestReservationSuccess')
);
const Main = React.lazy(() => import('pages/Main/Main'));
const ManagerJoin = React.lazy(() => import('pages/ManagerJoin/ManagerJoin'));
const ManagerSocialJoin = React.lazy(() => import('pages/ManagerSocialJoin/ManagerSocialJoin'));
const Login = React.lazy(() => import('pages/Login/Login'));
const ManagerMapDetail = React.lazy(() => import('pages/ManagerMapDetail/ManagerMapDetail'));
const ManagerMapEditor = React.lazy(() => import('pages/ManagerMapEditor/ManagerMapEditor'));
const ManagerReservation = React.lazy(() => import('pages/ManagerReservation/ManagerReservation'));
const ManagerSpaceEditor = React.lazy(() => import('pages/ManagerSpaceEditor/ManagerSpaceEditor'));
const GithubOAuthRedirect = React.lazy(() => import('pages/OAuthRedirect/GithubOAuthRedirect'));
const GoogleOAuthRedirect = React.lazy(() => import('pages/OAuthRedirect/GoogleOAuthRedirect'));
const GuestNonLoginReservationSearch = React.lazy(
  () => import('pages/GuestNonLoginReservationSearch/GuestNonLoginReservationSearch')
);

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
    path: PATH.LOGIN,
    component: <Login />,
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
    component: <GuestMapContainer />,
  },
  {
    path: PATH.GUEST_RESERVATION,
    component: <GuestReservation />,
  },
  {
    path: PATH.GUEST_RESERVATION_EDIT,
    component: <GuestReservation />,
  },
  {
    path: PATH.GUEST_RESERVATION_SUCCESS,
    component: <GuestReservationSuccess />,
  },
  {
    path: PATH.GUEST_NON_LOGIN_RESERVATION_SEARCH,
    component: <GuestNonLoginReservationSearch />,
  },
];

export const PRIVATE_ROUTES: PrivateRoute[] = [
  {
    path: PATH.MANAGER_MAP_LIST,
    component: <ManagerMapList />,
    redirectPath: PATH.LOGIN,
  },
  {
    path: PATH.MANAGER_RESERVATION,
    component: <ManagerReservation />,
    redirectPath: PATH.LOGIN,
  },
  {
    path: PATH.MANAGER_RESERVATION_EDIT,
    component: <ManagerReservation />,
    redirectPath: PATH.LOGIN,
  },
  {
    path: PATH.MANAGER_MAP_CREATE,
    component: <ManagerMapEditor />,
    redirectPath: PATH.LOGIN,
  },
  {
    path: PATH.MANAGER_MAP_EDIT,
    component: <ManagerMapEditor />,
    redirectPath: PATH.LOGIN,
  },
  {
    path: PATH.MANAGER_SPACE_EDIT,
    component: <ManagerSpaceEditor />,
    redirectPath: PATH.LOGIN,
  },
  {
    path: PATH.GUEST_MAIN,
    component: <GuestMain />,
    redirectPath: PATH.LOGIN,
  },
  {
    path: PATH.MANAGER_MAP_DETAIL,
    component: <ManagerMapDetail />,
    redirectPath: PATH.LOGIN,
  },
];
