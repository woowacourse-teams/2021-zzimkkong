import dayjs from 'dayjs';
import { useContext } from 'react';
import { useHistory, useLocation, useParams } from 'react-router-dom';
import Button from 'components/Button/Button';
import Header from 'components/Header/Header';
import Layout from 'components/Layout/Layout';
import ManagerReservationListItem from 'components/ManagerReservationListItem/ManagerReservationListItem';
import MESSAGE from 'constants/message';
import PATH, { HREF } from 'constants/path';
import { LOCAL_STORAGE_KEY } from 'constants/storage';
import useSpaceEntry from 'hooks/query/useSpaceEntry';
import { AccessTokenContext } from 'providers/AccessTokenProvider';
import { Reservation } from 'types/common';
import { formatDate, formatTimePrettier } from 'utils/datetime';
import { setLocalStorageItem } from 'utils/localStorage';
import { getReservationStatus } from 'utils/reservation';
import * as Styled from './SpaceEntry.styles';

interface SpaceEntryParams {
  sharingSpaceId: string;
}

const SpaceEntry = (): JSX.Element => {
  const { accessToken } = useContext(AccessTokenContext);
  const history = useHistory();
  const location = useLocation();
  const { sharingSpaceId } = useParams<SpaceEntryParams>();

  const queryParams = new URLSearchParams(location.search);
  const defaultDescription = queryParams.get('description') ?? '';

  const getSpaceEntry = useSpaceEntry(
    { sharingSpaceId },
    {
      retry: false,
    }
  );

  const spaceEntry = getSpaceEntry.data?.data;

  const today = formatDate(dayjs().tz());
  const todayReservations: Reservation[] = spaceEntry?.todayReservations ?? [];

  const handleReserve = () => {
    if (!spaceEntry) return;

    if (!accessToken) {
      setLocalStorageItem({
        key: LOCAL_STORAGE_KEY.AFTER_LOGIN_PATH,
        item: location.pathname + location.search,
      });
      history.push(PATH.LOGIN);
      return;
    }

    history.push({
      pathname: HREF.GUEST_RESERVATION(spaceEntry.sharingMapId),
      state: {
        mapId: spaceEntry.mapId,
        spaceId: spaceEntry.spaceId,
        selectedDate: today,
        defaultDescription,
      },
    });
  };

  if (getSpaceEntry.isLoading) {
    return (
      <>
        <Header />
        <Layout>
          <Styled.LoadingContainer>
            <Styled.LoadingMessage>공간 정보를 불러오는 중입니다...</Styled.LoadingMessage>
          </Styled.LoadingContainer>
        </Layout>
      </>
    );
  }

  if (getSpaceEntry.isError || !spaceEntry) {
    return (
      <>
        <Header />
        <Layout>
          <Styled.ErrorContainer>
            <Styled.ErrorMessage>
              {'공간 정보를 찾을 수 없습니다.\n올바른 QR 코드인지 확인해주세요.'}
            </Styled.ErrorMessage>
            <Styled.HomeLink to={PATH.MAIN}>홈으로 이동하기</Styled.HomeLink>
          </Styled.ErrorContainer>
        </Layout>
      </>
    );
  }

  return (
    <>
      <Header />
      <Layout>
        <Styled.Container>
          <Styled.SpaceHeader>
            <Styled.MapName>{spaceEntry.mapName}</Styled.MapName>
            <Styled.SpaceNameWrapper>
              <Styled.ColorDot color={spaceEntry.spaceColor} size="large" />
              <Styled.SpaceName>{spaceEntry.spaceName}</Styled.SpaceName>
            </Styled.SpaceNameWrapper>
            <Styled.StatusBadge available={spaceEntry.reservationEnable}>
              {spaceEntry.reservationEnable ? '예약 가능' : '예약 불가'}
            </Styled.StatusBadge>
            <br />
            <Styled.MapLink to={HREF.GUEST_MAP(spaceEntry.sharingMapId)}>
              전체 맵 보기
            </Styled.MapLink>
          </Styled.SpaceHeader>

          <Styled.ReserveButtonWrapper>
            <Button
              variant="primary"
              size="large"
              fullWidth
              disabled={!spaceEntry.reservationEnable}
              onClick={handleReserve}
            >
              {accessToken ? '즉석 예약하기' : '로그인 후 예약하기'}
            </Button>
          </Styled.ReserveButtonWrapper>

          {spaceEntry.settings.length > 0 && (
            <Styled.Section>
              <Styled.SectionTitle>예약 가능 시간</Styled.SectionTitle>
              <Styled.SettingInfo>
                {spaceEntry.settings.map((setting, index) => (
                  <Styled.SettingText key={index}>
                    {setting.settingStartTime.slice(0, 5)} ~ {setting.settingEndTime.slice(0, 5)}
                    {' ('}
                    최소 {formatTimePrettier(setting.reservationMinimumTimeUnit)}, 최대{' '}
                    {formatTimePrettier(setting.reservationMaximumTimeUnit)}
                    {')'}
                  </Styled.SettingText>
                ))}
              </Styled.SettingInfo>
            </Styled.Section>
          )}

          <Styled.Section>
            <Styled.SectionTitle>오늘의 예약 현황</Styled.SectionTitle>
            {todayReservations.length === 0 && (
              <Styled.Message>{MESSAGE.RESERVATION.SUGGESTION}</Styled.Message>
            )}
            {todayReservations.length > 0 && (
              <Styled.ReservationList role="list">
                {todayReservations.map((reservation) => (
                  <ManagerReservationListItem
                    key={reservation.id}
                    reservation={reservation}
                    status={getReservationStatus(
                      reservation.startDateTime,
                      reservation.endDateTime
                    )}
                  />
                ))}
              </Styled.ReservationList>
            )}
          </Styled.Section>
        </Styled.Container>
      </Layout>
    </>
  );
};

export default SpaceEntry;
