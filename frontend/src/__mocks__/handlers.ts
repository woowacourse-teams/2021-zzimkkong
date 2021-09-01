import { DefaultRequestBody, rest, RestHandler } from 'msw';
import { BASE_URL } from 'constants/api';
import { MapItemResponse } from 'types/response';
import { formatDate } from 'utils/datetime';
import { ManagerSpaceAPI, Reservation } from './../types/common';
import { guestMaps, reservations, spaces } from './mockData';

const ENDPOINT = process.env.DEPLOY_ENV !== 'production' ? BASE_URL.DEV : BASE_URL.PROD;

type GetGuestMapResponseBody = MapItemResponse;

interface GetGuestSpacesResponseBody {
  spaces: ManagerSpaceAPI[];
}

interface GetGuestSpacesParams {
  mapId: number;
}

interface GetReservationsResponseBody {
  reservations: Reservation[];
}

interface GetReservationsParams {
  mapId: number;
  spaceId: number;
}

interface GuestReservationRequestBody {
  reservation: {
    startDateTime: Date;
    endDateTime: Date;
    password: string;
    name: string;
    description: string;
  };
}
interface PostReservationParams {
  mapId: number;
  spaceId: number;
}

interface DeleteReservationRequestBody {
  password: string;
}

interface DeleteReservationParams {
  mapId: number;
  spaceId: number;
  reservationId: number;
}

const handlers: RestHandler[] = [
  rest.get<never, GetGuestMapResponseBody, never>(`${ENDPOINT}/guests/maps`, (req, res, ctx) => {
    const sharingMapId = req.url.searchParams.get('sharingMapId');

    if (!sharingMapId) {
      return res(ctx.status(400));
    }

    if (!guestMaps[sharingMapId]) {
      return res(ctx.status(404));
    }

    return res(ctx.status(200), ctx.json(guestMaps[sharingMapId]));
  }),

  rest.get<never, GetGuestSpacesResponseBody, GetGuestSpacesParams>(
    `${ENDPOINT}/guests/maps/:mapId/spaces`,
    (req, res, ctx) => {
      const { mapId } = req.params;

      return res(
        ctx.status(200),
        ctx.json({
          spaces: spaces[mapId] ?? [],
        })
      );
    }
  ),

  rest.get<never, GetReservationsResponseBody, GetReservationsParams>(
    `${ENDPOINT}/guests/maps/:mapId/spaces/:spaceId/reservations`,
    (req, res, ctx) => {
      const { mapId, spaceId } = req.params;
      const date = req.url.searchParams.get('date');

      return res(
        ctx.status(200),
        ctx.json({
          reservations:
            reservations?.[mapId]?.[spaceId].filter((reservation) => {
              return formatDate(new Date(reservation.startDateTime)) === date;
            }) ?? [],
        })
      );
    }
  ),

  rest.post<GuestReservationRequestBody, DefaultRequestBody, PostReservationParams>(
    `${ENDPOINT}/guests/maps/:mapId/spaces/:spaceId/reservations`,
    (req, res, ctx) => {
      const { mapId, spaceId } = req.params;

      if (!spaces[mapId] || !!spaces[mapId].find(({ id }) => id === spaceId)) {
        return res(ctx.status(400));
      }

      return res(ctx.status(201));
    }
  ),

  rest.delete<DeleteReservationRequestBody, DefaultRequestBody, DeleteReservationParams>(
    `${ENDPOINT}/guests/maps/:mapId/spaces/:spaceId/reservations/:reservationId`,
    (req, res, ctx) => {
      const { mapId, spaceId, reservationId } = req.params;
      const { password } = req.body;

      if (!spaces[mapId] || !!spaces[mapId].find(({ id }) => id === spaceId)) {
        return res(ctx.status(400));
      }

      const target = reservations[mapId][spaceId].find(
        (reservation) => reservation.id === Number(reservationId)
      );

      if (!target) return res(ctx.status(400));

      if (password !== target.password) {
        return res(ctx.status(400));
      }

      return res(ctx.status(204));
    }
  ),
];

export default handlers;
