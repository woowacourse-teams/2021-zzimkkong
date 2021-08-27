import { ReactNode } from 'react';
import GuestMap from 'pages/GuestMap/GuestMap';
import GuestReservation from 'pages/GuestReservation/GuestReservation';
import GuestReservationEdit from 'pages/GuestReservationEdit/GuestReservationEdit';
import Main from 'pages/Main/Main';
import ManagerJoin from 'pages/ManagerJoin/ManagerJoin';
import ManagerLogin from 'pages/ManagerLogin/ManagerLogin';
import ManagerMain from 'pages/ManagerMain/ManagerMain';
import ManagerMapCreate from 'pages/ManagerMapCreate/ManagerMapCreate';
import ManagerSpaceEdit from 'pages/ManagerSpaceEdit/ManagerSpaceEdit';
import PATH from './path';

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
    path: PATH.GUEST_MAP,
    component: <GuestMap />,
  },
  {
    path: PATH.GUEST_RESERVATION,
    component: <GuestReservation />,
  },
  {
    path: PATH.GUEST_RESERVATION_EDIT,
    component: <GuestReservationEdit />,
  },
];

export const PRIVATE_ROUTES: PrivateRoute[] = [
  {
    path: PATH.MANAGER_MAIN,
    component: <ManagerMain />,
    redirectPath: PATH.MANAGER_LOGIN,
  },
  {
    path: PATH.MANAGER_MAP_CREATE,
    component: <ManagerMapCreate />,
    redirectPath: PATH.MANAGER_LOGIN,
  },
  {
    path: PATH.MANAGER_MAP_EDIT,
    component: <ManagerMapCreate />,
    redirectPath: PATH.MANAGER_LOGIN,
  },
  {
    path: PATH.MANAGER_SPACE_EDIT,
    component: <ManagerSpaceEdit />,
    redirectPath: PATH.MANAGER_LOGIN,
  },
];
