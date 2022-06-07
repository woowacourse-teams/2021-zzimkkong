import dayjs, { Dayjs } from 'dayjs';
import { ChangeEventHandler, Dispatch, InputHTMLAttributes, SetStateAction } from 'react';
import IconButton from 'components/IconButton/IconButton';
import { formatDate, formatDateWithDay } from 'utils/datetime';
import * as Styled from './DateInput.styles';

export interface Props extends Omit<InputHTMLAttributes<HTMLInputElement>, 'type' | 'value'> {
  date: Dayjs;
  hasBackground?: boolean;
  setDate: Dispatch<SetStateAction<Dayjs>>;
}

const DateInput = ({ date, hasBackground = true, setDate, ...props }: Props): JSX.Element => {
  const onChange: ChangeEventHandler<HTMLInputElement> = (event) => {
    setDate(dayjs(event.target.value).tz());
  };

  const onClickPrev = () => {
    setDate(dayjs(date).subtract(1, 'day'));
  };

  const onClickNext = () => {
    setDate(dayjs(date).add(1, 'day'));
  };

  return (
    <Styled.Container hasBackground={hasBackground}>
      <IconButton onClick={onClickPrev}>
        <Styled.PrimaryArrowLeftIcon width="100%" height="100%" />
      </IconButton>
      <Styled.DateWrapper>
        <Styled.DateText>{formatDateWithDay(date)}</Styled.DateText>
        <Styled.DateInput type="date" onChange={onChange} value={formatDate(date)} {...props} />
        <Styled.GrayCalendarIcon />
      </Styled.DateWrapper>
      <IconButton onClick={onClickNext}>
        <Styled.PrimaryArrowRightIcon width="100%" height="100%" />
      </IconButton>
    </Styled.Container>
  );
};

export default DateInput;
