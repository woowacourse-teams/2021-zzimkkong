import {
  formatDate,
  formatDateWithDay,
  formatTimePrettier,
  formatTimeWithSecond,
  isPastDay,
  isPastTime,
} from 'utils/datetime';

describe('datetime 관련 util 함수 테스트', () => {
  describe('formatDate Function Test', () => {
    test('Date가 YYYY-MM-DD 형식으로 변환 된다.', () => {
      expect(formatDate(new Date('2021-11-17T00:00:00.000Z'))).toBe('2021-11-17');
      expect(formatDate(new Date('Wed Oct 13 2021 18:48:47 GMT+0900 (한국 표준시)'))).toBe(
        '2021-10-13'
      );
    });
  });

  describe('formatDateWithDay Function Test', () => {
    test('Date가 YYYY/MM/DD (MON) 형식으로 변환 된다.', () => {
      expect(formatDateWithDay(new Date('2021-11-17T00:00:00.000Z'))).toBe('2021/11/17 (WED)');
      expect(formatDateWithDay(new Date('Wed Oct 13 2021 18:48:47 GMT+0900 (한국 표준시)'))).toBe(
        '2021/10/13 (WED)'
      );
    });
  });

  describe('formatTimeWithSecond Function Test', () => {
    test('Date가 HH:MM:SS 형태로 변환 된다.', () => {
      expect(formatTimeWithSecond(new Date('Wed Oct 13 2021 00:01:00'))).toBe('00:01:00');
      expect(formatTimeWithSecond(new Date('Wed Oct 13 2021 18:48:47'))).toBe('18:48:47');
    });
  });

  describe('formatTimePrettier Function Test', () => {
    test('Number를 hh시간 mm분 형태로 변환 된다.', () => {
      expect(formatTimePrettier(60)).toBe('1시간');
      expect(formatTimePrettier(90)).toBe('1시간 30분');
    });
  });

  describe('isPastTime Function Test', () => {
    test('Date가 현재 시점보다 이전이면 true를 반환한다.', () => {
      expect(isPastTime(new Date('2020-11-17T00:00:00.000Z'))).toBe(true);
      expect(isPastTime(new Date('Wed Oct 13 2020 18:48:47 GMT+0900 (한국 표준시)'))).toBe(true);
    });

    test('Date가 현재 시점보다 이후면 false를 반환한다.', () => {
      expect(isPastTime(new Date('2030-11-17T00:00:00.000Z'))).toBe(false);
      expect(isPastTime(new Date('Wed Oct 13 2030 18:48:47 GMT+0900 (한국 표준시)'))).toBe(false);
    });
  });

  describe('isPastDay Function Test', () => {
    test('Date가 현재 날짜보다 이전 날짜이면 true를 반환한다.', () => {
      expect(isPastDay(new Date('2020-11-17T00:00:00.000Z'))).toBe(true);
      expect(isPastDay(new Date('Wed Oct 13 2020 18:48:47 GMT+0900 (한국 표준시)'))).toBe(true);
    });

    test('Date가 현재 날짜보다 이후 날짜이면 false를 반환한다.', () => {
      expect(isPastDay(new Date('2030-11-17T00:00:00.000Z'))).toBe(false);
      expect(isPastDay(new Date('Wed Oct 13 2030 18:48:47 GMT+0900 (한국 표준시)'))).toBe(false);
    });
  });
});