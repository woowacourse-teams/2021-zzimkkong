import { ReactNode } from 'react';
import GuestMain from 'pages/GuestMain/GuestMain';
import GuestReservation from 'pages/GuestReservation/GuestReservation';
import GuestReservationEdit from 'pages/GuestReservationEdit/GuestReservationEdit';
import ManagerJoin from 'pages/ManagerJoin/ManagerJoin';
import ManagerLogin from 'pages/ManagerLogin/ManagerLogin';
import ManagerMain from 'pages/ManagerMain/ManagerMain';
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
    path: PATH.MANAGER_LOGIN,
    component: <ManagerLogin />,
  },
  {
    path: PATH.MANAGER_JOIN,
    component: <ManagerJoin />,
  },
  {
    path: PATH.GUEST_MAIN,
    component: <GuestMain />,
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
];
