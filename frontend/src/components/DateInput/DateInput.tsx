import { ChangeEventHandler, Dispatch, InputHTMLAttributes, SetStateAction } from 'react';
import { ReactComponent as ArrowLeftIcon } from 'assets/svg/arrow-left.svg';
import { ReactComponent as ArrowRightIcon } from 'assets/svg/arrow-right.svg';
import { ReactComponent as CalendarIcon } from 'assets/svg/calendar.svg';
import IconButton from 'components/IconButton/IconButton';
import { formatDate, formatDateWithDay } from 'utils/datetime';
import * as Styled from './DateInput.styles';

export interface Props extends Omit<InputHTMLAttributes<HTMLInputElement>, 'type' | 'value'> {
  date: Date;
  setDate: Dispatch<SetStateAction<Date>>;
}

const DateInput = ({ date, setDate, ...props }: Props): JSX.Element => {
  const onChange: ChangeEventHandler<HTMLInputElement> = (event) => {
    setDate(new Date(event.target.value));
  };

  const onClickPrev = () => {
    setDate(new Date(date.setDate(date.getDate() - 1)));
  };

  const onClickNext = () => {
    setDate(new Date(date.setDate(date.getDate() + 1)));
  };

  return (
    <Styled.Container>
      <IconButton variant="primary" onClick={onClickPrev}>
        <ArrowLeftIcon />
      </IconButton>
      <Styled.DateWrapper>
        <Styled.DateText>{formatDateWithDay(date)}</Styled.DateText>
        <Styled.DateInput type="date" onChange={onChange} value={formatDate(date)} {...props} />
        <CalendarIcon />
      </Styled.DateWrapper>
      <IconButton variant="primary" onClick={onClickNext}>
        <ArrowRightIcon />
      </IconButton>
    </Styled.Container>
  );
};

export default DateInput;
