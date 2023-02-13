import { createContext, useMemo, useState } from 'react';
import DATE from 'constants/date';
import SPACE from 'constants/space';
import useGuestSpaceAvailable from 'hooks/query/useGuestSpaceAvailable';
import useGuestSpaces from 'hooks/query/useGuestSpaces';
import useInputs from 'hooks/useInputs';
import useTimePicker from 'hooks/useTimePicker';
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
  const [formValues, onChangeFormValues] = useInputs<GuestMapForm>(initialValue.formValues);

  const [selectedSpaceId, setSelectedSpaceId] = useState('');

  const timePicker = useTimePicker({
    step: SPACE.RESERVATION.MIN_STEP,
    initialStartTime: undefined,
    initialEndTime: undefined,
  });

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

  const availableSpaceList =
    getSpaceAvailable.data?.data.spaces
      .filter((space) => space.isAvailable)
      .map((space) => spacesMap[space.spaceId]) ?? [];

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
