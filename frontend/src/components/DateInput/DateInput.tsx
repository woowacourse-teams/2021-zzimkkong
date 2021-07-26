import { ChangeEventHandler, InputHTMLAttributes, useState } from 'react';
import { ReactComponent as ArrowLeftIcon } from 'assets/svg/arrow-left.svg';
import { ReactComponent as ArrowRightIcon } from 'assets/svg/arrow-right.svg';
import { ReactComponent as CalendarIcon } from 'assets/svg/calendar.svg';
import IconButton from 'components/IconButton/IconButton';
import { formatDateWithDay } from 'utils/datetime';
import * as Styled from './DateInput.styles';

export type Props = Omit<InputHTMLAttributes<HTMLInputElement>, 'type'>;

const DateInput = ({ ...props }: Props): JSX.Element => {
  const [dateText, setDateText] = useState<string>(
    formatDateWithDay(new Date(props.value as string))
  );

  const onChange: ChangeEventHandler<HTMLInputElement> = (event) => {
    setDateText(formatDateWithDay(new Date(event.target.value)));
  };

  return (
    <Styled.Container>
      <IconButton variant="primary">
        <ArrowLeftIcon />
      </IconButton>
      <Styled.DateWrapper>
        <Styled.DateText>{dateText}</Styled.DateText>
        <Styled.DateInput type="date" onChange={onChange} {...props} />
        <CalendarIcon />
      </Styled.DateWrapper>
      <IconButton variant="primary">
        <ArrowRightIcon />
      </IconButton>
    </Styled.Container>
  );
};

export default DateInput;
