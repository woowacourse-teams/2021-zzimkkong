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
  const spaceList: Space[] = [
    {
      spaceId: 1,
      spaceName: '백엔드 강의실',
      textPosition: 'bottom',
      color: '#FED7D9',
      coordinate: {
        x: 100,
        y: 90,
      },
    },
    {
      spaceId: 2,
      spaceName: '프론트 강의실1',
      textPosition: 'bottom',
      color: '#FED7D9',
      coordinate: {
        x: 560,
        y: 40,
      },
    },
    {
      spaceId: 3,
      spaceName: '프론트 강의실2',
      textPosition: 'bottom',
      color: '#FED7D9',
      coordinate: {
        x: 560,
        y: 140,
      },
    },
    {
      spaceId: 4,
      spaceName: '회의실1',
      textPosition: 'bottom',
      color: '#FFE3AC',
      coordinate: {
        x: 29,
        y: 229,
      },
    },
    {
      spaceId: 5,
      spaceName: '회의실2',
      textPosition: 'bottom',
      color: '#FFE3AC',
      coordinate: {
        x: 88,
        y: 229,
      },
    },
    {
      spaceId: 6,
      spaceName: '회의실3',
      textPosition: 'bottom',
      color: '#FFE3AC',
      coordinate: {
        x: 510,
        y: 220,
      },
    },
    {
      spaceId: 7,
      spaceName: '회의실4',
      textPosition: 'bottom',
      color: '#FFE3AC',
      coordinate: {
        x: 584,
        y: 220,
      },
    },
    {
      spaceId: 8,
      spaceName: '회의실5',
      textPosition: 'bottom',
      color: '#FFE3AC',
      coordinate: {
        x: 668,
        y: 335,
      },
    },
    {
      spaceId: 9,
      spaceName: '트랙방',
      textPosition: 'bottom',
      color: '#D8FBCC',
      coordinate: {
        x: 259,
        y: 336,
      },
    },
    {
      spaceId: 10,
      spaceName: '페어룸1',
      textPosition: 'left',
      color: '#CCDFFB',
      coordinate: {
        x: 208,
        y: 289,
      },
    },
    {
      spaceId: 11,
      spaceName: '페어룸2',
      textPosition: 'left',
      color: '#CCDFFB',
      coordinate: {
        x: 208,
        y: 318,
      },
    },
    {
      spaceId: 12,
      spaceName: '페어룸3',
      textPosition: 'left',
      color: '#CCDFFB',
      coordinate: {
        x: 208,
        y: 347,
      },
    },
    {
      spaceId: 13,
      spaceName: '페어룸4',
      textPosition: 'left',
      color: '#CCDFFB',
      coordinate: {
        x: 208,
        y: 376,
      },
    },
    {
      spaceId: 14,
      spaceName: '페어룸5',
      textPosition: 'left',
      color: '#CCDFFB',
      coordinate: {
        x: 208,
        y: 404,
      },
    },
  ];

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
                  <Panel.Inner>예약</Panel.Inner>
                </Styled.ReservationLink>
              )}
              <Panel.Inner>
                <Panel.Title>{selectedSpace.spaceName}</Panel.Title>
              </Panel.Inner>
            </Panel.Header>
            <Panel.Content>
              <>
                {getReservations.isLoadingError && (
                  <Panel.Inner>
                    <Styled.Message>
                      예약 목록을 불러오는 데 문제가 생겼어요!
                      <br />
                      새로 고침으로 다시 시도해주세요.
                    </Styled.Message>
                  </Panel.Inner>
                )}
                {getReservations.isLoading && !getReservations.isLoadingError && (
                  <Panel.Inner>
                    <Styled.Message>불러오는 중입니다...</Styled.Message>
                  </Panel.Inner>
                )}
                {getReservations.isSuccess && reservations?.length === 0 && (
                  <Panel.Inner>
                    <Styled.Message>오늘의 첫 예약을 잡아보세요!</Styled.Message>
                  </Panel.Inner>
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
