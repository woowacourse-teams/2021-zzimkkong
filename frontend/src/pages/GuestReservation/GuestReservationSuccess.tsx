import { Redirect, useLocation, useParams } from 'react-router';
import { ReactComponent as Logo } from 'assets/svg/logo.svg';
import Header from 'components/Header/Header';
import Layout from 'components/Layout/Layout';
import { HREF } from 'constants/path';
import { GuestMapState } from 'pages/GuestMap/GuestMap';
import { GuestPageURLParams } from 'types/guest';
import { formatDateWithDay, formatTime } from 'utils/datetime';
import * as Styled from './GuestReservationSuccess.styles';

interface GuestReservationSuccessState extends GuestMapState {
  spaceName: string;
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

  const { spaceId, spaceName, targetDate, reservation } = location.state;
  const { name, description, startDateTime, endDateTime } = reservation;

  const date = formatDateWithDay(new Date(startDateTime));
  const start = formatTime(new Date(startDateTime));
  const end = formatTime(new Date(endDateTime));

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
                state: { spaceId, targetDate },
              }}
            >
              맵으로 돌아가기
            </Styled.PageLink>
          </Styled.MessageContainer>

          <Styled.Info>
            <Styled.InfoRow>
              <Styled.InfoLabel>공간이름</Styled.InfoLabel>
              <Styled.InfoText>{spaceName}</Styled.InfoText>
            </Styled.InfoRow>
            <Styled.InfoRow>
              <Styled.InfoLabel>예약자명</Styled.InfoLabel>
              <Styled.InfoText>{name}</Styled.InfoText>
            </Styled.InfoRow>
            <Styled.InfoRow>
              <Styled.InfoLabel>사용목적</Styled.InfoLabel>
              <Styled.InfoText>{description}</Styled.InfoText>
            </Styled.InfoRow>
            <Styled.InfoRow>
              <Styled.InfoLabel>예약일시</Styled.InfoLabel>
              <Styled.InfoText>
                {date} {start} - {end}
              </Styled.InfoText>
            </Styled.InfoRow>
          </Styled.Info>
        </Styled.Container>
      </Layout>
    </>
  );
};

export default GuestReservationSuccess;
