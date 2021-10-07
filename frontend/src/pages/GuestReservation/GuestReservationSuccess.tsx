import { Redirect, useLocation, useParams } from 'react-router';
import { ReactComponent as Logo } from 'assets/svg/logo.svg';
import Header from 'components/Header/Header';
import Layout from 'components/Layout/Layout';
import { HREF } from 'constants/path';
import { Space } from 'types/common';
import { GuestPageURLParams } from 'types/guest';
import { formatDateWithDay, formatTime } from 'utils/datetime';
import * as Styled from './GuestReservationSuccess.styles';

export interface GuestReservationSuccessState {
  space: Space;
  targetDate: Date;
  reservation: {
    name: string;
    description: string;
    startDateTime: Date;
    endDateTime: Date;
  };
}

const GuestReservationSuccess = (): JSX.Element => {
  const { sharingMapId } = useParams<GuestPageURLParams>();
  const location = useLocation<GuestReservationSuccessState>();

  if (!location.state) return <Redirect to={HREF.GUEST_MAP(sharingMapId)} />;

  const { space, reservation, targetDate } = location.state;

  const reservationDate = formatDateWithDay(new Date(reservation.startDateTime));
  const reservationStartTime = formatTime(new Date(reservation.startDateTime));
  const reservationEndTime = formatTime(new Date(reservation.endDateTime));

  return (
    <>
      <Header />
      <Layout>
        <Styled.Container>
          <Logo />
          <Styled.MessageContainer>
            <Styled.Message>예약이 완료되었습니다!</Styled.Message>
            <Styled.PageLink
              to={{
                pathname: HREF.GUEST_MAP(sharingMapId),
                state: { spaceId: space?.id, targetDate },
              }}
            >
              맵으로 돌아가기
            </Styled.PageLink>
          </Styled.MessageContainer>

          <Styled.Info>
            <Styled.InfoRow>
              <Styled.InfoLabel>공간이름</Styled.InfoLabel>
              <Styled.InfoText>{space?.name}</Styled.InfoText>
            </Styled.InfoRow>
            <Styled.InfoRow>
              <Styled.InfoLabel>예약자명</Styled.InfoLabel>
              <Styled.InfoText>{reservation?.name}</Styled.InfoText>
            </Styled.InfoRow>
            <Styled.InfoRow>
              <Styled.InfoLabel>사용목적</Styled.InfoLabel>
              <Styled.InfoText>{reservation?.description}</Styled.InfoText>
            </Styled.InfoRow>
            <Styled.InfoRow>
              <Styled.InfoLabel>예약일시</Styled.InfoLabel>
              <Styled.InfoText>
                {reservationDate} {reservationStartTime} - {reservationEndTime}
              </Styled.InfoText>
            </Styled.InfoRow>
          </Styled.Info>
        </Styled.Container>
      </Layout>
    </>
  );
};

export default GuestReservationSuccess;
