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
import { Reservation } from 'types/common';
import * as Styled from './UserMain.styles';

const UserMain = (): JSX.Element => {
  const reservations: Reservation[] = [
    {
      id: 5,
      startDateTime: '2021-07-15T10:00:00',
      endDateTime: '2021-07-15T11:00:00',
      name: '찜꽁',
      description: '찜꽁 5차 회의',
    },
    {
      id: 1,
      startDateTime: '2021-07-15T13:30:00',
      endDateTime: '2021-07-15T14:30:00',
      name: '샐리',
      description: '회의입니다.',
    },
    {
      id: 10,
      name: '체프',
      description: '맛있는 커피를 내리는 법',
      startDateTime: '2021-07-15T20:00:00',
      endDateTime: '2021-07-15T21:00:00',
    },
  ];

  const spaces = [
    {
      id: 1,
      spaceName: '백엔드 강의실',
      color: '#FED7D9',
      coordinate: {
        x: 100,
        y: 90,
      },
    },
    {
      id: 2,
      spaceName: '프론트 강의실1',
      color: '#FED7D9',
      coordinate: {
        x: 560,
        y: 40,
      },
    },
    {
      id: 3,
      spaceName: '프론트 강의실2',
      color: '#FED7D9',
      coordinate: {
        x: 560,
        y: 140,
      },
    },
    {
      id: 4,
      spaceName: '회의실1',
      color: '#FFE3AC',
      coordinate: {
        x: 29,
        y: 229,
      },
    },
    {
      id: 5,
      spaceName: '회의실2',
      color: '#FFE3AC',
      coordinate: {
        x: 88,
        y: 229,
      },
    },
    {
      id: 6,
      spaceName: '회의실3',
      color: '#FFE3AC',
      coordinate: {
        x: 510,
        y: 220,
      },
    },
    {
      id: 7,
      spaceName: '회의실4',
      color: '#FFE3AC',
      coordinate: {
        x: 584,
        y: 220,
      },
    },
    {
      id: 8,
      spaceName: '회의실5',
      color: '#FFE3AC',
      coordinate: {
        x: 668,
        y: 335,
      },
    },
    {
      id: 9,
      spaceName: '트랙방',
      color: '#D8FBCC',
      coordinate: {
        x: 259,
        y: 336,
      },
    },
    {
      id: 10,
      spaceName: '페어룸1',
      color: '#CCDFFB',
      coordinate: {
        x: 208,
        y: 289,
      },
    },
    {
      id: 11,
      spaceName: '페어룸2',
      color: '#CCDFFB',
      coordinate: {
        x: 208,
        y: 318,
      },
    },
    {
      id: 12,
      spaceName: '페어룸3',
      color: '#CCDFFB',
      coordinate: {
        x: 208,
        y: 347,
      },
    },
    {
      id: 13,
      spaceName: '페어룸4',
      color: '#CCDFFB',
      coordinate: {
        x: 208,
        y: 376,
      },
    },
    {
      id: 14,
      spaceName: '페어룸5',
      color: '#CCDFFB',
      coordinate: {
        x: 208,
        y: 404,
      },
    },
  ];

  return (
    <>
      <Header />
      <Layout>
        <Styled.PageTitle>우아한테크코스 교육장</Styled.PageTitle>
        <DateInput value="2021-07-16" />
        <Styled.MapContainer>
          <Styled.Map>
            {spaces &&
              spaces.map(({ id, spaceName, coordinate }) => (
                <PinRadio name="luther" coordinate={coordinate} text={spaceName} />
              ))}
            <Luther />
          </Styled.Map>
        </Styled.MapContainer>
        <Styled.PanelContainer>
          <Panel>
            <Panel.Header bgColor="#FFEEC4">
              <Styled.ReservationLink to={PATH.RESERVATION}>예약</Styled.ReservationLink>
              <Panel.Title>회의실1</Panel.Title>
            </Panel.Header>
            <Panel.Content>
              {reservations && (
                <Styled.ReservationList>
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
            </Panel.Content>
          </Panel>
        </Styled.PanelContainer>
      </Layout>
    </>
  );
};

export default UserMain;
