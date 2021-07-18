import { InputHTMLAttributes } from 'react';
import { ReactComponent as CalendarIcon } from 'assets/svg/calendar.svg';
import * as Styled from './DateInput.styles';

export type Props = InputHTMLAttributes<HTMLInputElement>;

const DateInput = ({ value }: Props): JSX.Element => {
  return (
    <Styled.DateWrapper>
      <Styled.DateInput type="date" value={value} />
      <CalendarIcon />
    </Styled.DateWrapper>
  );
};

export default DateInput;
