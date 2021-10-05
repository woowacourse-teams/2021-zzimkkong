import React, { MouseEventHandler, useState } from 'react';
import * as Styled from './TimePicker.styles';
import TimePickerOptions from './TimePickerOptions';

export interface Props {
  label?: string;
  step?: 1 | 5 | 10 | 15 | 20 | 30;
  defaultStartTime?: Date;
  defaultEndTime?: Date;
}

export interface Time {
  midday: '오전' | '오후';
  hour: number;
  minute: number;
}

export interface Range {
  start: Time | null;
  end: Time | null;
}

type SelectedTime = keyof Range | null;

const generateTo12Hour = (hour: number) => (hour > 12 ? hour % 12 : hour);

const generateDateToTime = (time: Date, step: Props['step'] = 1): Time => {
  const minute = Math.ceil(time.getMinutes() / step) * step;
  const hour = minute < 60 ? time.getHours() : time.getHours() + 1;
  const midday = hour < 12 ? '오전' : '오후';

  return {
    midday,
    hour: generateTo12Hour(hour),
    minute: minute % 60,
  };
};

const TimePicker = ({ label, step = 1, defaultStartTime, defaultEndTime }: Props): JSX.Element => {
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
            startTime.hour < 11 ? startTime.midday : startTime.midday === '오전' ? '오후' : '오전',
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

    setSelectedTime(key as SelectedTime);
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

  const getTimeText = (key: keyof Range) => {
    const time = range[key];

    if (time === null) {
      return key === 'start' ? '시작시간' : '종료시간';
    }

    return `${time.midday} ${String(time.hour).padStart(2, '0')} :
    ${String(time.minute).padStart(2, '0')}`;
  };

  return (
    <Styled.Container>
      <Styled.TimeContainer labelText={label} isOptionOpen={selectedTime !== null}>
        <Styled.TimeButton
          type="button"
          aria-label="시작시간"
          name="start"
          onClick={onClickTimeButton}
          selected={selectedTime === 'start'}
        >
          {getTimeText('start')}
        </Styled.TimeButton>
        <span>~</span>
        <Styled.TimeButton
          type="button"
          aria-label="종료시간"
          name="end"
          onClick={onClickTimeButton}
          selected={selectedTime === 'end'}
        >
          {getTimeText('end')}
        </Styled.TimeButton>
      </Styled.TimeContainer>

      {selectedTime !== null && (
        <Styled.OptionsContainer>
          <TimePickerOptions time={range[selectedTime]} step={step} onChange={onChange} />
        </Styled.OptionsContainer>
      )}
    </Styled.Container>
  );
};

export default TimePicker;
