import { rest, RestHandler } from 'msw';
import { formatDate } from 'utils/datetime';
import { Reservation } from './../types/common';
import { reservations } from './mockData';

const BASE_URL = 'https://zzimkkong-proxy.o-r.kr/api';

interface GetReservationsResponseBody {
  reservations: Reservation[];
}
interface GetReservationsParams {
  mapId: number;
  spaceId: number;
}

const handlers: RestHandler[] = [
  rest.get<never, GetReservationsResponseBody, GetReservationsParams>(
    `${BASE_URL}/guests/maps/:mapId/spaces/:spaceId/reservations`,
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
];

export default handlers;
