import { InputHTMLAttributes } from 'react';
import { ReactComponent as CalendarIcon } from 'assets/svg/calendar.svg';
import * as Styled from './DateInput.styles';

export type Props = Omit<InputHTMLAttributes<HTMLInputElement>, 'type'>;

const DateInput = ({ ...props }: Props): JSX.Element => {
  return (
    <Styled.DateWrapper>
      <Styled.DateInput type="date" {...props} />
      <CalendarIcon />
    </Styled.DateWrapper>
  );
};

export default DateInput;
