import { ReactNode } from 'react';
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

  const startHour = startDateTime.getHours();
  const startMinute = startDateTime.getMinutes();

  const endHour = endDateTime.getHours();
  const endMinute = endDateTime.getMinutes();

  const start = `${startHour < 10 ? `0${startHour}` : `${startHour}`}:${
    startMinute < 10 ? `0${startMinute}` : `${startMinute}`
  }`;

  const end = `${endHour < 10 ? `0${endHour}` : `${endHour}`}:${
    endMinute < 10 ? `0${endMinute}` : `${endMinute}`
  }`;

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
