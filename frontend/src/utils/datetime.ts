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
