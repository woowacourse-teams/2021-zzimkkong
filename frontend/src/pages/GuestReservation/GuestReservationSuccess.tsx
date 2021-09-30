import { Redirect, useLocation, useParams } from 'react-router';
import { ReactComponent as Logo } from 'assets/svg/logo.svg';
import Header from 'components/Header/Header';
import Layout from 'components/Layout/Layout';
import { HREF } from 'constants/path';
import { GuestMapState } from 'pages/GuestMap/GuestMap';
import { GuestPageURLParams } from 'types/guest';
import * as Styled from './GuestReservationSuccess.styles';

const GuestReservationSuccess = (): JSX.Element => {
  const { sharingMapId } = useParams<GuestPageURLParams>();
  const location = useLocation<GuestMapState>();

  if (!location.state) return <Redirect to={HREF.GUEST_MAP(sharingMapId)} />;

  return (
    <>
      <Header />
      <Layout>
        <Styled.Container>
          <Logo />
          <Styled.Message>예약이 완료되었습니다!</Styled.Message>
          <Styled.PageLink
            to={{
              pathname: HREF.GUEST_MAP(sharingMapId),
              state: {
                spaceId: location.state.spaceId,
                targetDate: location.state.targetDate,
              },
            }}
          >
            맵으로 돌아가기
          </Styled.PageLink>
        </Styled.Container>
      </Layout>
    </>
  );
};

export default GuestReservationSuccess;
