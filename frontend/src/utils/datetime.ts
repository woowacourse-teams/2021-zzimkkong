import dayjs, { Dayjs, isDayjs } from 'dayjs';
import { Midday, Time } from 'types/time';

// Note: YYYY-MM-DD 형식으로 변환함
export const formatDate = (value: Date | Dayjs): string => {
  if (isDayjs(value)) {
    return value.format('YYYY-MM-DD');
  }

  const year = value.getFullYear();
  const month = `${value.getMonth() + 1}`.padStart(2, '0');
  const date = `${value.getDate()}`.padStart(2, '0');

  return `${year}-${month}-${date}`;
};

const DAY = ['SUN', 'MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT'];

// Note: YYYY/MM/DD (MON) 형식으로 변환함
export const formatDateWithDay = (value: Date | Dayjs): string => {
  if (isDayjs(value)) {
    return value.format('YYYY-MM-DD (ddd)').toUpperCase();
  }

  const year = value.getFullYear();
  const month = `${value.getMonth() + 1}`.padStart(2, '0');
  const date = `${value.getDate()}`.padStart(2, '0');
  const day = value.getDay();

  return `${year}/${month}/${date} (${DAY[day]})`;
};

// Note: HH:MM (24시간 기준) 형태로 변환함
export const formatTime = (time: Date | Time | Dayjs): string => {
  if (isDayjs(time)) {
    return time.format('HH:mm');
  }

  if (time instanceof Date) {
    const hour = time.getHours();
    const minute = time.getMinutes();

    return `${hour < 10 ? `0${hour}` : `${hour}`}:${minute < 10 ? `0${minute}` : `${minute}`}`;
  }

  const minute = `${time.minute}`.padStart(2, '0');

  if (time.hour === 12) {
    return `${time.midday === Midday.AM ? '00' : '12'}:${minute}`;
  }

  const hour = time.midday === Midday.AM ? `${time.hour}`.padStart(2, '0') : `${time.hour + 12}`;

  return `${hour}:${minute}`;
};

// Note: HH:MM:SS (24시간 기준) 형태로 변환함
export const formatTimeWithSecond = (time: Date | Time | Dayjs): string => {
  if (isDayjs(time)) {
    return time.format('HH:mm:ss');
  }

  if (time instanceof Date) {
    const hour = time.getHours();
    const minute = time.getMinutes();
    const second = `${time.getSeconds()}`.padStart(2, '0');

    return `${hour < 10 ? `0${hour}` : `${hour}`}:${
      minute < 10 ? `0${minute}` : `${minute}`
    }:${second}`;
  }

  const minute = `${time.minute}`.padStart(2, '0');
  const second = '00';

  if (time.hour === 12) {
    return `${time.midday === Midday.AM ? '00' : '12'}:${minute}:${second}`;
  }

  const hour = time.midday === Midday.AM ? `${time.hour}`.padStart(2, '0') : `${time.hour + 12}`;

  return `${hour}:${minute}:${second}`;
};

// Note: hh시간 mm분 형태로 변환함
export const formatTimePrettier = (minutes: number): string => {
  const hour = Math.floor(minutes / 60);
  const minute = minutes % 60;

  return `${hour ? ` ${hour}시간` : ''}${minute ? ' ' : ''}${minute ? `${minute}분` : ''}`;
};

export const isPastTime = (time: Date | Dayjs, baseDate: Date = new Date()): boolean => {
  if (isDayjs(time)) {
    return +time < +dayjs(baseDate).tz();
  }

  return time.getTime() < baseDate.getTime();
};

export const isPastDate = (time: Date | Dayjs, baseDate: Date = new Date()): boolean => {
  if (isDayjs(time)) {
    return +time < +dayjs(baseDate).tz() - 1000 * 60 * 60 * 24;
  }

  return time.getTime() < baseDate.getTime() - 1000 * 60 * 60 * 24;
};

export const isFutureDate = (time: Date | Dayjs, baseDate: Date = new Date()): boolean => {
  if (isDayjs(time)) {
    return +time > +dayjs(baseDate).tz() + 1000 * 60 * 60 * 24;
  }

  return time.getTime() > baseDate.getTime() + 1000 * 60 * 60 * 24;
};

// YYYY:MM:DD:HH:mm:ss -> N
export const convertTimeToMinutes = (time: Date): number => {
  return time.getHours() * 60 + time.getMinutes();
};

// HH:mm:ss -> N
export const convertSettingTimeToMinutes = (time: string): number => {
  const [hours, minutes] = time.split(':');

  return Number(hours) * 60 + Number(minutes);
};
