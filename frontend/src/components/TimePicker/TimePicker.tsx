import React, { MouseEventHandler } from 'react';
import { Range } from '../../hooks/useTimePicker';
import * as Styled from './TimePicker.styles';
import TimePickerOptions from './TimePickerOptions';

interface Props {
  label?: string;
  step?: 1 | 5 | 10 | 15 | 20 | 30;
  range: Range;
  selectedTime: keyof Range | null;
  onClick: MouseEventHandler<HTMLButtonElement>;
  onChange: (name: string) => (event: React.ChangeEvent<HTMLInputElement>) => void;
  onCloseOptions: () => void;
}

const TimePicker = ({
  label,
  step = 1,
  range,
  selectedTime,
  onClick,
  onChange,
  onCloseOptions,
}: Props): JSX.Element => {
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
          onClick={onClick}
          selected={selectedTime === 'start'}
        >
          {getTimeText('start')}
        </Styled.TimeButton>
        <span>~</span>
        <Styled.TimeButton
          type="button"
          aria-label="종료시간"
          name="end"
          onClick={onClick}
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

      {selectedTime !== null && <Styled.Dimmer onClick={onCloseOptions} />}
    </Styled.Container>
  );
};

export default TimePicker;
