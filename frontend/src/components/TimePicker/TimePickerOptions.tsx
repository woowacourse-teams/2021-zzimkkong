import { ChangeEventHandler, useEffect, useRef } from 'react';
import { Time } from './TimePicker';
import * as Styled from './TimePickerOptions.styles';

interface Props {
  time: Time;
  setTime: (value: Time) => void;
  step?: 1 | 5 | 10 | 15 | 20 | 30;
  onChangeMidday: ChangeEventHandler<HTMLInputElement>;
  onChangeHour: ChangeEventHandler<HTMLInputElement>;
  onChangeMinute: ChangeEventHandler<HTMLInputElement>;
}

const TimePickerOptions = ({
  time,
  setTime,
  step = 1,
  onChangeMidday,
  onChangeHour,
  onChangeMinute,
}: Props): JSX.Element => {
  const middayRef = useRef<HTMLDivElement | null>(null);
  const hourRef = useRef<HTMLDivElement | null>(null);
  const minuteRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    if (middayRef.current !== null) {
      middayRef.current.scrollTo(0, time.midday === '오전' ? 0 : 32);
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
            onChange={onChangeMidday}
            checked={time.midday === '오전'}
          />
          <Styled.OptionText>오전</Styled.OptionText>
        </Styled.Option>
        <Styled.Option tabIndex={0}>
          <Styled.Radio
            type="radio"
            name="midday"
            value="오후"
            onChange={onChangeMidday}
            checked={time.midday === '오후'}
          />
          <Styled.OptionText>오후</Styled.OptionText>
        </Styled.Option>
      </Styled.OptionContainer>
      <Styled.OptionContainer ref={hourRef}>
        {Array(12)
          .fill(undefined)
          .map((_, index) => (
            <Styled.Option key={`${index}-hour`} tabIndex={0}>
              <Styled.Radio
                type="radio"
                name="hour"
                value={index + 1}
                onChange={onChangeHour}
                checked={index + 1 === time.hour}
              />
              <Styled.OptionText>{String(index + 1).padStart(2, '0')} 시</Styled.OptionText>
            </Styled.Option>
          ))}
      </Styled.OptionContainer>
      <Styled.OptionContainer ref={minuteRef}>
        {Array(60 / step)
          .fill(undefined)
          .map((_, index) => (
            <Styled.Option key={`${index}-minute`} tabIndex={0}>
              <Styled.Radio
                type="radio"
                name="minute"
                value={index * step}
                onChange={onChangeMinute}
                checked={index * step === time.minute}
              />
              <Styled.OptionText>{String(index * step).padStart(2, '0')} 분</Styled.OptionText>
            </Styled.Option>
          ))}
      </Styled.OptionContainer>
    </Styled.OptionsContainer>
  );
};

export default TimePickerOptions;
