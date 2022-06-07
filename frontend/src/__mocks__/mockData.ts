import { MapItemResponse } from 'types/response';
import { ManagerSpaceAPI } from './../types/common';

interface GuestMaps {
  [sharingMapId: string]: MapItemResponse;
}

interface Spaces {
  [mapId: number]: ManagerSpaceAPI[];
}

interface Reservation {
  id: number;
  startDateTime: string;
  endDateTime: string;
  password: string;
  name: string;
  description: string;
}

interface Reservations {
  [mapId: number]: {
    [spaceId: number]: Reservation[];
  };
}

export const guestMaps: GuestMaps = {
  JMTGR: {
    mapId: 1,
    mapName: 'GUEST_TEST_MAP',
    mapDrawing:
      '{"width":800,"height":600,"mapElements":[{"id":2,"type":"rect","stroke":"#333333","points":["210,90","650,230"]},{"id":3,"type":"rect","stroke":"#333333","width":440,"height":140,"x":210,"y":90,"points":["210, 90","650, 230"]}]}',
    thumbnail: '',
    sharingMapId: 'JMTGR',
    notice: null,
  },
};

export const spaces: Spaces = {
  1: [
    {
      id: 1,
      name: 'testSpace',
      color: '#EB3933',
      description: 'testMap',
      area: '{"shape":"rect","x":210,"y":90,"width":440,"height":140}',
      settings: {
        availableStartTime: '07:00:00',
        availableEndTime: '23:00:00',
        reservationTimeUnit: 10,
        reservationMinimumTimeUnit: 10,
        reservationMaximumTimeUnit: 1440,
        reservationEnable: true,
        enabledDayOfWeek: {
          monday: true,
          tuesday: true,
          wednesday: true,
          thursday: true,
          friday: true,
          saturday: true,
          sunday: true,
        },
      },
    },
  ],
};

export const reservations: Reservations = {
  1: {
    1: [
      {
        id: 1,
        startDateTime: '2031-07-01T00:00:00',
        endDateTime: '2031-07-01T01:00:00',
        name: '찜꽁',
        description: '찜꽁 5차 회의',
        password: '1111',
      },
      {
        id: 2,
        startDateTime: '2031-07-01T03:30:00',
        endDateTime: '2031-07-01T04:30:00',
        name: '샐리',
        description: '찜꽁 데모데이 회의.',
        password: '1111',
      },
      {
        id: 3,
        startDateTime: '2031-08-30T00:00:00',
        endDateTime: '2031-08-30T01:00:00',
        name: '체프',
        description: '찜꽁 165차 회의',
        password: '1111',
      },
      {
        id: 4,
        startDateTime: '2031-08-30T03:30:00',
        endDateTime: '2031-08-30T04:30:00',
        name: '썬',
        description: '찜꽁 A시리즈 투자 관련 회의.',
        password: '1111',
      },
    ],
  },

  2: {
    1: [
      {
        id: 10,
        startDateTime: '2031-07-01T00:00:00',
        endDateTime: '2031-07-01T01:00:00',
        name: '체프',
        description: '야식 먹자',
        password: '1111',
      },
      {
        id: 11,
        startDateTime: '2031-07-02T03:30:00',
        endDateTime: '2031-07-02T04:30:00',
        name: '샐리',
        description: '바다와 함께하는 페어프로그래밍.',
        password: '1111',
      },
    ],
    2: [
      {
        id: 12,
        startDateTime: '2031-07-01T00:00:00',
        endDateTime: '2031-07-01T01:00:00',
        name: '유조',
        description: '제천 여행 계획',
        password: '1111',
      },
      {
        id: 13,
        startDateTime: '2031-07-02T15:30:00',
        endDateTime: '2031-07-02T16:30:00',
        name: '썬',
        description: '도예 클래스',
        password: '1111',
      },
    ],
  },
};
