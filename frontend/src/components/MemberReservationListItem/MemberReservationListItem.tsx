import dayjs from 'dayjs';
import { ReactNode } from 'react';
import { MemberReservation } from 'types/common';
import * as Styled from './MemberReservationListItem.styles';

export interface Props {
  reservation: MemberReservation;
  control?: ReactNode;
  onClick?: () => void;
}

const MemberReservationListItem = ({ reservation, control, onClick }: Props): JSX.Element => {
  return (
    <Styled.Container role="listitem">
      <Styled.InfoContainer onClick={onClick}>
        <Styled.Description>{reservation.description}</Styled.Description>
        <Styled.DetailContainer>
          <Styled.Date>
            {dayjs(new Date(reservation.startDateTime))
              .format('YYYY년 MM월 DD일 (ddd) HH:mm')
              .toUpperCase()}{' '}
            - {dayjs(new Date(reservation.endDateTime)).format('HH:mm')}
          </Styled.Date>
          <Styled.Space spaceColor={reservation.spaceColor}>
            {reservation.spaceName} · {reservation.mapName}
          </Styled.Space>
        </Styled.DetailContainer>
      </Styled.InfoContainer>
      {control && <Styled.ControlWrapper>{control}</Styled.ControlWrapper>}
    </Styled.Container>
  );
};

export default MemberReservationListItem;
