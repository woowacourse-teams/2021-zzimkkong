import dayjs from 'dayjs';
import { ReactNode } from 'react';
import { Reservation, ReservationStatus } from 'types/common';
import { formatTime } from 'utils/datetime';
import * as Styled from './ReservationItem.styled';

export interface Props {
  reservation: Reservation;
  control?: ReactNode;
  status?: ReservationStatus;
}

const ReservationItem = ({ reservation, control, status, ...props }: Props): JSX.Element => {
  const { name, description, startDateTime, endDateTime } = reservation;

  const start = formatTime(dayjs(startDateTime).tz());
  const end = formatTime(dayjs(endDateTime).tz());

  return (
    <Styled.Item role="listitem" {...props}>
      <Styled.InfoWrapper status={status}>
        {status && <Styled.StatusBadge status={status}>{status}</Styled.StatusBadge>}
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

export default ReservationItem;
