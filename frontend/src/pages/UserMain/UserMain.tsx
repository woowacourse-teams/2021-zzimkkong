import { useLocation } from 'react-router-dom';
import { ReactComponent as Luther } from 'assets/svg/luther.svg';
import { ReactComponent as More } from 'assets/svg/more.svg';
import Button from 'components/Button/Button';
import DateInput from 'components/DateInput/DateInput';
import Header from 'components/Header/Header';
import Layout from 'components/Layout/Layout';
import Panel from 'components/Panel/Panel';
import PinRadio from 'components/PinRadio/PinRadio';
import ReservationListItem from 'components/ReservationListItem/ReservationListItem';
import PATH from 'constants/path';
import useInput from 'hooks/useInput';
import useReservations from 'hooks/useReservations';
import { Reservation, Space } from 'types/common';
import { formatDate } from 'utils/datetime';
import * as Styled from './UserMain.styles';

export interface UserMainState {
  spaceId?: Space['spaceId'];
  targetDate?: Date;
}

const UserMain = (): JSX.Element => {
  // Note: 루터회관 14층으로 상정하고 구현. 추후 useSpaces로 대체 필요
  const mapId = 1;

  const location = useLocation<UserMainState>();
  const spaceId = location.state?.spaceId;
  const targetDate = location.state?.targetDate;
  const now = new Date();
  const todayDate = new Date(now.getFullYear(), now.getMonth(), now.getDate());

  const [date, onChangeDate] = useInput(formatDate(targetDate ?? new Date()));
  const [selectedSpaceId, onChangeSelectedSpaceId] = useInput(`${spaceId ?? spaceList[0].spaceId}`);
  const getReservations = useReservations({
    mapId,
    spaceId: Number(selectedSpaceId),
    date,
  });
  const reservations = getReservations.data?.data?.reservations ?? [];

  const selectedSpace =
    spaceList.find((space) => space.spaceId === Number(selectedSpaceId)) ?? spaceList[0];

  return (
    <>
      <Header />
      <Layout>
        <Styled.PageTitle>우아한테크코스 교육장</Styled.PageTitle>
        <DateInput value={date} onChange={onChangeDate} />
        <Styled.MapContainer>
          <Styled.Map>
            {spaceList?.map(({ spaceId, spaceName, coordinate, textPosition }) => (
              <PinRadio
                key={spaceId}
                name={`map-${mapId}`}
                coordinate={coordinate}
                text={spaceName}
                textPosition={textPosition}
                value={spaceId}
                checked={Number(selectedSpaceId) === spaceId}
                onChange={onChangeSelectedSpaceId}
              />
            ))}
            <Luther />
          </Styled.Map>
        </Styled.MapContainer>
        <Styled.PanelContainer>
          <Panel>
            <Panel.Header bgColor={selectedSpace.color}>
              {new Date(date) > todayDate && (
                <Styled.ReservationLink
                  to={{
                    pathname: PATH.RESERVATION,
                    state: {
                      mapId,
                      spaceId: Number(selectedSpaceId),
                      spaceName: selectedSpace.spaceName,
                      selectedDate: date,
                    },
                  }}
                >
                  예약
                </Styled.ReservationLink>
              )}
              <Panel.Title>{selectedSpace.spaceName}</Panel.Title>
            </Panel.Header>
            <Panel.Content>
              <>
                {getReservations.isLoadingError && (
                  <Panel.Inner>
                    <Styled.Message>
                  <Styled.Message>
                    예약 목록을 불러오는 데 문제가 생겼어요!
                    <br />
                    새로 고침으로 다시 시도해주세요.
                  </Styled.Message>
                )}
                {getReservations.isLoading && !getReservations.isLoadingError && (
                  <Panel.Inner>
                    <Styled.Message>불러오는 중입니다...</Styled.Message>
                  <Styled.Message>불러오는 중입니다...</Styled.Message>
                )}
                {getReservations.isSuccess && reservations?.length === 0 && (
                  <Panel.Inner>
                    <Styled.Message>오늘의 첫 예약을 잡아보세요!</Styled.Message>
                  <Styled.Message>오늘의 첫 예약을 잡아보세요!</Styled.Message>
                )}
                {getReservations.isSuccess && reservations.length > 0 && (
                  <Styled.ReservationList role="list">
                    {reservations.map((reservation: Reservation) => (
                      <ReservationListItem
                        key={reservation.id}
                        reservation={reservation}
                        control={
                          <Button variant="text" size="small">
                            <More />
                          </Button>
                        }
                      />
                    ))}
                  </Styled.ReservationList>
                )}
              </>
            </Panel.Content>
          </Panel>
        </Styled.PanelContainer>
      </Layout>
    </>
  );
};

export default UserMain;
