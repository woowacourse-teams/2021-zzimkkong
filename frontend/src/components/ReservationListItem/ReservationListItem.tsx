import { ReactNode } from 'react';
import { formatTime } from 'utils/datetime';
import * as Styled from './ReservationListItem.styles';

export interface Props {
  reservation: {
    name: string;
    description: string;
    startDateTime: Date;
    endDateTime: Date;
  };
  control?: ReactNode;
}

const ReservationListItem = ({ reservation, control }: Props): JSX.Element => {
  const { name, description, startDateTime, endDateTime } = reservation;

  const start = formatTime(startDateTime);
  const end = formatTime(endDateTime);

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
