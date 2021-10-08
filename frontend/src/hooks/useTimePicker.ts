import { MouseEventHandler, useState } from 'react';
import { Step } from 'components/TimePicker/TimePicker';
import { Midday, Time } from 'types/time';

type SelectedTime = keyof Range | null;

export interface Range {
  start: Time | null;
  end: Time | null;
}

interface Props {
  defaultStartTime?: Date;
  defaultEndTime?: Date;
  step?: Step;
}

const generateTo12Hour = (hour: number) => (hour > 12 ? hour % 12 : hour);

const generateDateToTime = (time: Date, step: Props['step'] = 1): Time => {
  const minute = Math.ceil(time.getMinutes() / step) * step;
  const hour = minute < 60 ? time.getHours() : time.getHours() + 1;
  const midday = hour < 12 ? Midday.AM : Midday.PM;

  return {
    midday,
    hour: generateTo12Hour(hour),
    minute: minute % 60,
  };
};

const useTimePicker = ({
  defaultStartTime,
  defaultEndTime,
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
    start: defaultStartTime ? generateDateToTime(defaultStartTime, step) : null,
    end: defaultEndTime ? generateDateToTime(defaultEndTime, step) : null,
  });

  const setInitialTime = (key: keyof Range) => {
    if (key === 'start' || (key === 'end' && range.start === null)) {
      const now = new Date();

      setRange((prev) => ({
        ...prev,
        [key]: generateDateToTime(now, step),
      }));
    }

    if (key === 'end' && range.start !== null) {
      const startTime = range.start;

      setRange((prev) => ({
        ...prev,
        end: {
          midday:
            startTime.hour < 11
              ? startTime.midday
              : startTime.midday === Midday.AM
              ? Midday.PM
              : Midday.AM,
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
