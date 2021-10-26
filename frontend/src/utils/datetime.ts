import { Midday, Time } from 'types/time';

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
export const formatTime = (time: Date | Time): string => {
  if (time instanceof Date) {
    const hour = time.getHours();
    const minute = time.getMinutes();

    return `${hour < 10 ? `0${hour}` : `${hour}`}:${minute < 10 ? `0${minute}` : `${minute}`}`;
  }

  const hour = time.midday === Midday.AM ? `${time.hour}`.padStart(2, '0') : `${time.hour + 12}`;
  const minute = `${time.minute}`.padStart(2, '0');

  return `${hour}:${minute}`;
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

export const isPastTime = (time: Date, baseDate: Date = new Date()): boolean => {
  return time.getTime() < baseDate.getTime();
};

export const isPastDate = (time: Date, baseDate: Date = new Date()): boolean => {
  return time.getTime() < baseDate.getTime() - 1000 * 60 * 60 * 24;
};

export const isFutureDate = (time: Date, baseDate: Date = new Date()): boolean => {
  return time.getTime() > baseDate.getTime() + 1000 * 60 * 60 * 24;
};
