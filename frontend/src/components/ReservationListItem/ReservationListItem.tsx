import { Attributes, ReactNode } from 'react';
import { Reservation } from 'types/common';
import { formatTime } from 'utils/datetime';
import * as Styled from './ReservationListItem.styles';

export interface Props {
  reservation: Reservation;
  control?: ReactNode;
}

const ReservationListItem = ({ reservation, control }: Props): JSX.Element => {
  const { name, description, startDateTime, endDateTime } = reservation;

  const start = formatTime(new Date(startDateTime));
  const end = formatTime(new Date(endDateTime));

  return (
    <Styled.Item role="listitem">
      <Styled.Info>
        <Styled.Name>{name}</Styled.Name>
        <Styled.Description>{description}</Styled.Description>
        <Styled.Time>
          {start} - {end}
        </Styled.Time>
      </Styled.Info>
      {control && <Styled.Control>{control}</Styled.Control>}
    </Styled.Item>
  );
};

export default ReservationListItem;
