export const getReservationStatus = (
  startDateTime: string,
  endDateTime: string
): 'using' | 'done' | undefined => {
  const now = new Date().getTime();
  const start = new Date(startDateTime).getTime();
  const end = new Date(endDateTime).getTime();

  if (start <= now && end >= now) return 'using';
  if (now > end) return 'done';
};
