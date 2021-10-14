import DATE from 'constants/date';

// Note: YYYY-MM-DD 형식으로 변환함
export const formatDate = (value: Date): string => {
  const year = value.getFullYear();
  const month = `${value.getMonth() + 1}`.padStart(2, '0');
  const date = `${value.getDate()}`.padStart(2, '0');

  return `${year}-${month}-${date}`;
};

const DAY = ['SUN', 'MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT'];

// Note: YYYY/MM/DD (MON) 형식으로 변환함
export const formatDateWithDay = (value: Date): string => {
  const year = value.getFullYear();
  const month = `${value.getMonth() + 1}`.padStart(2, '0');
  const date = `${value.getDate()}`.padStart(2, '0');
  const day = value.getDay();

  return `${year}/${month}/${date} (${DAY[day]})`;
};

// Note: HH:MM 형태로 변환함
export const formatTime = (time: Date): string => {
  const hour = time.getHours();
  const minute = time.getMinutes();

  return `${hour < 10 ? `0${hour}` : `${hour}`}:${minute < 10 ? `0${minute}` : `${minute}`}`;
};

// Note: HH:MM:SS 형태로 변환함
export const formatTimeWithSecond = (time: Date): string => {
  const hour = `${time.getHours()}`.padStart(2, '0');
  const minute = `${time.getMinutes()}`.padStart(2, '0');
  const second = `${time.getSeconds()}`.padStart(2, '0');

  return `${hour}:${minute}:${second}`;
};

// Note: hh시간 mm분 형태로 변환함
export const formatTimePrettier = (minutes: number): string => {
  const hour = Math.floor(minutes / 60);
  const minute = minutes % 60;

  return `${hour ? `${hour}시간` : ''}${minute ? ' ' : ''}${minute ? `${minute}분` : ''}`;
};

export const isPastTime = (time: Date): boolean => {
  return time.getTime() < new Date().getTime();
};

export const isPastDay = (time: Date): boolean => {
  return time.getTime() < new Date().getTime() - 1000 * 60 * 60 * 24;
};

export const isPastDayThanMinDay = (time: Date): boolean => {
  return time.getTime() < DATE.MIN_DATE.getTime();
};

export const isFutureDayThanMaxDay = (time: Date): boolean => {
  return time.getTime() > DATE.MAX_DATE.getTime();
};
