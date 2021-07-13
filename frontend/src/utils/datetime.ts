// Note: HH:MM 형태로 변환함
export const formatTime = (time: Date): string => {
  const hour = time.getHours();
  const minute = time.getMinutes();

  return `${hour < 10 ? `0${hour}` : `${hour}`}:${minute < 10 ? `0${minute}` : `${minute}`}`;
};
