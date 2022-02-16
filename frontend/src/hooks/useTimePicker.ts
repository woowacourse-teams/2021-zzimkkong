import dayjs, { Dayjs, isDayjs } from 'dayjs';
import { MouseEventHandler, useState } from 'react';
import { Step } from 'components/TimePicker/TimePicker';
import { Midday, Range, Time } from 'types/time';

type SelectedTime = keyof Range | null;

interface Props {
  initialStartTime?: Date;
  initialEndTime?: Date;
  step?: Step;
}

const generateTo12Hour = (hour: number) => {
  const result = hour > 12 ? hour % 12 : hour;

  return result === 0 ? 12 : result;
};

const generateDateToTime = (time: Date | Dayjs, step: Props['step'] = 1): Time => {
  const minute = isDayjs(time)
    ? Math.ceil(time.minute() / step) * step
    : Math.ceil(time.getMinutes() / step) * step;
  const hour = isDayjs(time)
    ? minute < 60
      ? time.hour()
      : time.hour() + 1
    : minute < 60
    ? time.getHours()
    : time.getHours() + 1;
  const midday = hour < 12 ? Midday.AM : Midday.PM;

  return {
    midday,
    hour: generateTo12Hour(hour),
    minute: minute % 60,
  };
};

const useTimePicker = ({
  initialStartTime,
  initialEndTime,
  step = 1,
}: Props): {
  range: Range;
  selectedTime: SelectedTime;
  onClick: MouseEventHandler<HTMLButtonElement>;
  onChange: (name: string) => (event: React.ChangeEvent<HTMLInputElement>) => void;
  onCloseOptions: () => void;
} => {
  const [selectedTime, setSelectedTime] = useState<SelectedTime>(null);
  const [range, setRange] = useState<Range>({
    start: initialStartTime ? generateDateToTime(dayjs(initialStartTime).tz(), step) : null,
    end: initialEndTime ? generateDateToTime(dayjs(initialEndTime).tz(), step) : null,
  });

  const setInitialTime = (key: keyof Range) => {
    if (key === 'start' || (key === 'end' && range.start === null)) {
      const now = dayjs().tz();

      setRange((prev) => ({
        ...prev,
        [key]: generateDateToTime(now, step),
      }));
    }

    if (key === 'end' && range.start !== null) {
      const startTime = range.start;
      const endMidday =
        startTime.hour !== 11
          ? startTime.midday
          : startTime.midday === Midday.AM
          ? Midday.PM
          : Midday.AM;

      setRange((prev) => ({
        ...prev,
        end: {
          midday: endMidday,
          hour: generateTo12Hour(startTime.hour + 1),
          minute: startTime.minute,
        },
      }));
    }
  };

  const onClickTimeButton: MouseEventHandler<HTMLButtonElement> = ({ currentTarget }) => {
    const key = currentTarget.name as keyof Range;

    if (selectedTime === key) {
      setSelectedTime(null);

      return;
    }

    if (range[key] === null) {
      setInitialTime(key);
    }

    setSelectedTime(key);
  };

  const onChange =
    (name: string) =>
    ({ target }: React.ChangeEvent<HTMLInputElement>) => {
      if (selectedTime === null) return;

      const value = name === 'midday' ? target.value : Number(target.value);

      setRange((prev) => ({
        ...prev,
        [selectedTime]: {
          ...prev[selectedTime],
          [name]: value,
        },
      }));
    };

  const onCloseOptions = () => {
    setSelectedTime(null);
  };

  return { range, selectedTime, onClick: onClickTimeButton, onChange, onCloseOptions };
};

export default useTimePicker;
