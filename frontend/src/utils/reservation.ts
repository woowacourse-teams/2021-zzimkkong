import { ReservationStatus } from 'types/common';

export const getReservationStatus = (
  startDateTime: string,
  endDateTime: string
): ReservationStatus | undefined => {
  const now = new Date().getTime();
  const start = new Date(startDateTime).getTime();
  const end = new Date(endDateTime).getTime();

  if (start <= now && end >= now) return ReservationStatus.using;
  if (now > end) return ReservationStatus.done;
};
