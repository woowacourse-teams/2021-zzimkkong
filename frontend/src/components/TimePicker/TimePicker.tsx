import React, { ChangeEventHandler, MouseEventHandler, useState } from 'react';
import * as Styled from './TimePicker.styles';
import TimePickerOptions from './TimePickerOptions';

export interface Props {
  label?: string;
  step?: 1 | 5 | 10 | 15 | 20 | 30;
  minTime?: Date;
  maxTime?: Date;
}

export interface Time {
  midday: '오전' | '오후';
  hour: number;
  minute: number;
}

interface Range {
  start: Time;
  end: Time;
}

type SelectedTime = keyof Range | null;

const now = new Date();

const TimePicker = ({ label, step = 1, minTime, maxTime }: Props): JSX.Element => {
  const [selectedTime, setSelectedTime] = useState<SelectedTime>(null);
  const [range, setRange] = useState<Range>({
    start: {
      midday: now.getHours() < 12 ? '오전' : '오후',
      hour: now.getHours() % 12,
      minute: 0,
    },
    end: {
      midday: now.getHours() + 1 < 12 ? '오전' : '오후',
      hour: (now.getHours() % 12) + 1,
      minute: 0,
    },
  });

  const onClickTimeButton: MouseEventHandler<HTMLButtonElement> = ({ currentTarget }) => {
    if (selectedTime === currentTarget.name) {
      setSelectedTime(null);

      return;
    }

    setSelectedTime(currentTarget.name as SelectedTime);
  };

  const onChangeMidday: ChangeEventHandler<HTMLInputElement> = ({ target }) => {
    if (selectedTime === null) return;

    setRange((prev) => ({
      ...prev,
      [selectedTime]: {
        ...prev[selectedTime],
        midday: target.value,
      },
    }));
  };

  const onChangeHour: ChangeEventHandler<HTMLInputElement> = ({ target }) => {
    if (selectedTime === null) return;

    setRange((prev) => ({
      ...prev,
      [selectedTime]: {
        ...prev[selectedTime],
        hour: Number(target.value),
      },
    }));
  };

  const onChangeMinute: ChangeEventHandler<HTMLInputElement> = ({ target }) => {
    if (selectedTime === null) return;

    setRange((prev) => ({
      ...prev,
      [selectedTime]: {
        ...prev[selectedTime],
        minute: Number(target.value),
      },
    }));
  };

  const setTime = (value: Time) => {
    if (selectedTime === null) return;

    setRange((prev) => ({
      ...prev,
      [selectedTime]: {
        ...value,
      },
    }));
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
          {range.start.midday} {String(range.start.hour).padStart(2, '0')} :{' '}
          {String(range.start.minute).padStart(2, '0')}
        </Styled.TimeButton>
        <span>~</span>
        <Styled.TimeButton
          type="button"
          aria-label="종료시간"
          name="end"
          onClick={onClickTimeButton}
          selected={selectedTime === 'end'}
        >
          {range.end.midday} {String(range.end.hour).padStart(2, '0')} :{' '}
          {String(range.end.minute).padStart(2, '0')}
        </Styled.TimeButton>
      </Styled.TimeContainer>

      {selectedTime !== null && (
        <Styled.OptionsContainer>
          <TimePickerOptions
            time={range[selectedTime]}
            setTime={setTime}
            step={step}
            onChangeMidday={onChangeMidday}
            onChangeHour={onChangeHour}
            onChangeMinute={onChangeMinute}
          />
        </Styled.OptionsContainer>
      )}
    </Styled.Container>
  );
};

export default TimePicker;
