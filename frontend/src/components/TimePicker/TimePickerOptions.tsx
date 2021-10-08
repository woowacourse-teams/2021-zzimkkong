import React, { useEffect, useRef } from 'react';
import { Midday, Time } from 'types/time';
import { Step } from './TimePicker';
import * as Styled from './TimePickerOptions.styles';

interface Props {
  time: Time | null;
  step?: Step;
  onChange: (name: string) => (event: React.ChangeEvent<HTMLInputElement>) => void;
}

const TimePickerOptions = ({ time, step = 1, onChange }: Props): JSX.Element => {
  const middayRef = useRef<HTMLDivElement>(null);
  const hourRef = useRef<HTMLDivElement>(null);
  const minuteRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (time === null) return;

    if (middayRef.current !== null) {
      middayRef.current.scrollTo(0, time.midday === Midday.AM ? 0 : 32);
    }

    if (hourRef.current !== null) {
      hourRef.current.scrollTo(0, (time.hour - 1) * 32);
    }

    if (minuteRef.current !== null) {
      minuteRef.current.scrollTo(0, (time.minute / step) * 32);
    }
  }, [step, middayRef, hourRef, minuteRef, time]);

  return (
    <Styled.OptionsContainer>
      <Styled.OptionContainer ref={middayRef}>
        <Styled.Option tabIndex={0}>
          <Styled.Radio
            type="radio"
            name="midday"
            value="오전"
            onChange={onChange('midday')}
            checked={time?.midday === Midday.AM}
          />
          <Styled.OptionText>오전</Styled.OptionText>
        </Styled.Option>
        <Styled.Option tabIndex={0}>
          <Styled.Radio
            type="radio"
            name="midday"
            value="오후"
            onChange={onChange('midday')}
            checked={time?.midday === Midday.PM}
          />
          <Styled.OptionText>오후</Styled.OptionText>
        </Styled.Option>
      </Styled.OptionContainer>
      <Styled.OptionContainer ref={hourRef}>
        {Array.from({ length: 12 }).map((_, index) => (
          <Styled.Option key={`${index}-hour`} tabIndex={0}>
            <Styled.Radio
              type="radio"
              name="hour"
              value={index + 1}
              onChange={onChange('hour')}
              checked={index + 1 === time?.hour}
            />
            <Styled.OptionText>{String(index + 1).padStart(2, '0')} 시</Styled.OptionText>
          </Styled.Option>
        ))}
      </Styled.OptionContainer>
      <Styled.OptionContainer ref={minuteRef}>
        {Array.from({ length: 60 / step }).map((_, index) => (
          <Styled.Option key={`${index}-minute`} tabIndex={0}>
            <Styled.Radio
              type="radio"
              name="minute"
              value={index * step}
              onChange={onChange('minute')}
              checked={index * step === time?.minute}
            />
            <Styled.OptionText>{String(index * step).padStart(2, '0')} 분</Styled.OptionText>
          </Styled.Option>
        ))}
      </Styled.OptionContainer>
    </Styled.OptionsContainer>
  );
};

export default TimePickerOptions;
