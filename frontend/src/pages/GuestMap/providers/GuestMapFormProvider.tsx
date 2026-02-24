import { createContext, useContext, useMemo, useState } from 'react';
import DATE from 'constants/date';
import SPACE from 'constants/space';
import useGuestSpaceAvailable from 'hooks/query/useGuestSpaceAvailable';
import useGuestSpaces from 'hooks/query/useGuestSpaces';
import useMember from 'hooks/query/useMember';
import useInputs from 'hooks/useInputs';
import useTimePicker from 'hooks/useTimePicker';
import { AccessTokenContext } from 'providers/AccessTokenProvider';
import { Space } from 'types/common';
import { SpaceResponse } from 'types/response';
import { Time } from 'types/time';
import { formatDate, formatTimeWithSecond } from 'utils/datetime';

interface GuestMapForm {
  description: string;
  password: string;
  date: string;
  name: string;
}

interface GuestMapFormProviderProps {
  mapId: number;
  children?: React.ReactNode;
}

export interface GuestMapFormProviderValue {
  formValues: GuestMapForm;
  onChangeFormValues?: (event: React.ChangeEvent<HTMLInputElement>) => void;
  selectedSpaceId: string;
  setSelectedSpaceId?: React.Dispatch<React.SetStateAction<string>>;
  timePicker?: ReturnType<typeof useTimePicker>;
  spaceList?: SpaceResponse[];
  availableSpaceList?: SpaceResponse[];
  spacesMap?: Record<number, SpaceResponse>;
}

const convertTimeToDate = (date: string, time: Time | null) => {
  if (time === null) {
    return null;
  }

  return `${date}T${formatTimeWithSecond(time)}${DATE.TIMEZONE_OFFSET}`;
};

const initialValue: GuestMapFormProviderValue = {
  formValues: {
    description: '',
    password: '',
    date: formatDate(new Date()),
    name: '',
  },
  selectedSpaceId: '',
};

export const GuestMapFormContext = createContext<GuestMapFormProviderValue>(initialValue);

const GuestMapFormProvider = ({ mapId, children }: GuestMapFormProviderProps) => {
  const { accessToken } = useContext(AccessTokenContext);
  const [formValues, onChangeFormValues] = useInputs<GuestMapForm>(initialValue.formValues);

  const [selectedSpaceId, setSelectedSpaceId] = useState('');

  const timePicker = useTimePicker({
    step: SPACE.RESERVATION.MIN_STEP,
    initialStartTime: undefined,
    initialEndTime: undefined,
  });

  const member = useMember({
    enabled: !!accessToken,
  });
  const userGroup = member.data?.data.group ?? null;

  const getSpaces = useGuestSpaces({ mapId });
  const spacesMap = useMemo(() => {
    if (!getSpaces.data?.data) {
      return {};
    }

    return getSpaces.data.data.spaces.reduce((prev, cur) => {
      prev[cur.id] = cur;
      return prev;
    }, {} as Record<Space['id'], SpaceResponse>);
  }, [getSpaces.data?.data]);

  const getSpaceAvailable = useGuestSpaceAvailable(
    {
      mapId,
      startDateTime: convertTimeToDate(formValues.date, timePicker.range.start)!,
      endDateTime: convertTimeToDate(formValues.date, timePicker.range.end)!,
    },
    { enabled: !!timePicker.range.start && !!timePicker.range.end }
  );

  const spaceList = getSpaces.data?.data.spaces ?? [];

  // 권한 체크 함수
  const hasPermission = (space: SpaceResponse): boolean => {
    // allowedGroups가 비어있으면 모든 사용자 예약 가능
    if (!space.allowedGroups || space.allowedGroups.length === 0) {
      return true;
    }

    // 로그인하지 않은 사용자는 그룹 제한이 있는 공간 예약 불가
    if (!userGroup) {
      return false;
    }

    // 사용자 그룹이 허용된 그룹에 포함되어 있는지 확인
    return space.allowedGroups.includes(userGroup);
  };

  const availableSpaceList =
    getSpaceAvailable.data?.data.spaces
      .filter((space) => space.isAvailable)
      .map((space) => spacesMap[space.spaceId])
      .filter((space) => hasPermission(space)) ?? [];

  const value: GuestMapFormProviderValue = {
    formValues,
    onChangeFormValues,
    selectedSpaceId,
    setSelectedSpaceId,
    timePicker,
    spaceList,
    availableSpaceList,
    spacesMap,
  };

  return <GuestMapFormContext.Provider value={value}>{children}</GuestMapFormContext.Provider>;
};

export default GuestMapFormProvider;
