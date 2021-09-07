import { useEffect } from 'react';
import { useHistory, useLocation, useParams } from 'react-router-dom';
import Header from 'components/Header/Header';
import Layout from 'components/Layout/Layout';
import PageHeader from 'components/PageHeader/PageHeader';
import ReservationListItem from 'components/ReservationListItem/ReservationListItem';
import MESSAGE from 'constants/message';
import useGuestReservations from 'hooks/useGuestReservations';
import { GuestMapState } from 'pages/GuestMap/GuestMap';
import { MapItem, Reservation, ScrollPosition, Space } from 'types/common';
import * as Styled from './GuestReservation.styles';
import ReservationForm from './ReservationForm';

interface GuestReservationState {
  mapId: number;
  space: Space;
  selectedDate: string;
  scrollPosition: ScrollPosition;
  reservation?: Reservation;
}

interface URLParameter {
  sharingMapId: MapItem['sharingMapId'];
}

const GuestReservation = (): JSX.Element => {
  const location = useLocation<GuestReservationState>();
  const history = useHistory<GuestMapState>();
  const { sharingMapId } = useParams<URLParameter>();

  const { mapId, space, selectedDate, scrollPosition, reservation } = location.state;

  if (!mapId || !space) history.replace(`/guest/${sharingMapId}`);

  const getReservations = useGuestReservations({ mapId, spaceId: space.id, date: selectedDate });
  const reservations = getReservations.data?.data?.reservations ?? [];

  useEffect(() => {
    return history.listen((location) => {
      if (
        location.pathname === `/guest/${sharingMapId}` ||
        location.pathname === `/guest/${sharingMapId}/`
      ) {
        location.state = {
          spaceId: space.id,
          targetDate: new Date(selectedDate),
          scrollPosition,
        };
      }
    });
  }, [history, scrollPosition, selectedDate, space, sharingMapId]);

  return (
    <>
      <Header />
      <Layout>
        <Styled.PageHeader title="공간 이름" data-testid="spaceName">
          <Styled.ColorDot color={space.color} />
          {space.name}
        </Styled.PageHeader>
        <ReservationForm
          mapId={mapId}
          space={space}
          selectedDate={selectedDate}
          sharingMapId={sharingMapId}
          reservation={reservation}
        />
        <Styled.Section>
          <PageHeader title={`${selectedDate}${selectedDate && '의'} 예약 목록`} />
          {getReservations.isLoadingError && (
            <Styled.Message>{MESSAGE.RESERVATION.ERROR}</Styled.Message>
          )}
          {getReservations.isLoading && !getReservations.isLoadingError && (
            <Styled.Message>{MESSAGE.RESERVATION.PENDING}</Styled.Message>
          )}
          {getReservations.isSuccess && reservations.length === 0 && (
            <Styled.Message>{MESSAGE.RESERVATION.SUGGESTION}</Styled.Message>
          )}
          {getReservations.isSuccess && reservations.length > 0 && (
            <Styled.ReservationList role="list">
              {reservations?.map((reservation) => (
                <ReservationListItem key={reservation.id} reservation={reservation} />
              ))}
            </Styled.ReservationList>
          )}
        </Styled.Section>
      </Layout>
    </>
  );
};

export default GuestReservation;
