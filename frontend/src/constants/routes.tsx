import { ReactNode } from 'react';
import GuestMain from 'pages/GuestMain/GuestMain';
import GuestReservation from 'pages/GuestReservation/GuestReservation';
import GuestReservationEdit from 'pages/GuestReservationEdit/GuestReservationEdit';
import ManagerJoin from 'pages/ManagerJoin/ManagerJoin';
import ManagerLogin from 'pages/ManagerLogin/ManagerLogin';
import ManagerMain from 'pages/ManagerMain/ManagerMain';

interface Route {
  path: string;
  component: ReactNode;
}

export const PATH = {
  MANAGER_MAIN: '/',
  MANAGER_LOGIN: '/login',
  MANAGER_JOIN: '/join',
  GUEST_MAIN: '/guest',
  GUEST_RESERVATION: '/guest/reservation',
  GUEST_RESERVATION_EDIT: '/guest/reservation/edit',
};

const ROUTES: Route[] = [
  {
    path: PATH.MANAGER_MAIN,
    component: <ManagerMain />,
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

export default ROUTES;
