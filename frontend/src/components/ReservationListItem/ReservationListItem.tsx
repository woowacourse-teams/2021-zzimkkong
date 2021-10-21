import { ReactNode } from 'react';
import { Reservation } from 'types/common';
import { formatTime } from 'utils/datetime';
import * as Styled from './ReservationListItem.styles';

export interface Props {
  reservation: Reservation;
  control?: ReactNode;
  status?: 'using' | 'done';
}

const statusText = {
  using: '사용 중',
  done: '사용 완료',
};

const ReservationListItem = ({ reservation, control, status, ...props }: Props): JSX.Element => {
  const { name, description, startDateTime, endDateTime } = reservation;

  const start = formatTime(new Date(startDateTime));
  const end = formatTime(new Date(endDateTime));

  return (
    <Styled.Item role="listitem" {...props}>
      <Styled.InfoWrapper status={status}>
        {status && <Styled.StatusBadge status={status}>{statusText[status]}</Styled.StatusBadge>}
        <Styled.Info>
          <Styled.Name>
            <span>{name}</span>
          </Styled.Name>
          <Styled.Description status={status}>{description}</Styled.Description>
          <Styled.Time>
            <span>
              {start} - {end}
            </span>
          </Styled.Time>
        </Styled.Info>
      </Styled.InfoWrapper>
      {control && <Styled.Control>{control}</Styled.Control>}
    </Styled.Item>
  );
};

export default ReservationListItem;
