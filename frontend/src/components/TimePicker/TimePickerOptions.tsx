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
  const optionRef = useRef<HTMLLabelElement>(null);

  useEffect(() => {
    if (!time || !optionRef.current) return;

    const optionHeight = optionRef.current.offsetHeight;

    if (middayRef.current) {
      middayRef.current.scrollTo(0, time.midday === Midday.AM ? 0 : optionHeight);
    }

    if (hourRef.current) {
      hourRef.current.scrollTo(0, (time.hour - 1) * optionHeight);
    }

    if (minuteRef.current) {
      minuteRef.current.scrollTo(0, (time.minute / step) * optionHeight);
    }
  }, [step, middayRef, hourRef, minuteRef, time]);

  return (
    <Styled.OptionsContainer>
      <Styled.OptionContainer ref={middayRef}>
        <Styled.Option ref={optionRef}>
          <Styled.Radio
            type="radio"
            name="midday"
            value="오전"
            onChange={onChange('midday')}
            checked={time?.midday === Midday.AM}
            tabIndex={0}
          />
          <Styled.OptionText>오전</Styled.OptionText>
        </Styled.Option>
        <Styled.Option>
          <Styled.Radio
            type="radio"
            name="midday"
            value="오후"
            onChange={onChange('midday')}
            checked={time?.midday === Midday.PM}
            tabIndex={0}
          />
          <Styled.OptionText>오후</Styled.OptionText>
        </Styled.Option>
      </Styled.OptionContainer>
      <Styled.OptionContainer ref={hourRef}>
        {Array.from({ length: 12 }).map((_, index) => (
          <Styled.Option key={`${index}-hour`}>
            <Styled.Radio
              type="radio"
              name="hour"
              value={index + 1}
              onChange={onChange('hour')}
              checked={index + 1 === time?.hour}
              tabIndex={0}
            />
            <Styled.OptionText>{String(index + 1).padStart(2, '0')} 시</Styled.OptionText>
          </Styled.Option>
        ))}
      </Styled.OptionContainer>
      <Styled.OptionContainer ref={minuteRef}>
        {Array.from({ length: 60 / step }).map((_, index) => (
          <Styled.Option key={`${index}-minute`}>
            <Styled.Radio
              type="radio"
              name="minute"
              value={index * step}
              onChange={onChange('minute')}
              checked={index * step === time?.minute}
              tabIndex={0}
            />
            <Styled.OptionText>{String(index * step).padStart(2, '0')} 분</Styled.OptionText>
          </Styled.Option>
        ))}
      </Styled.OptionContainer>
    </Styled.OptionsContainer>
  );
};

export default TimePickerOptions;
