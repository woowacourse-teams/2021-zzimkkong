import { useEffect } from 'react';
import { useHistory, useLocation, useParams } from 'react-router-dom';
import Header from 'components/Header/Header';
import Layout from 'components/Layout/Layout';
import { GuestMapState } from 'pages/GuestMap/GuestMap';
import { MapItem, Reservation, ScrollPosition, Space } from 'types/common';
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
        <ReservationForm
          mapId={mapId}
          space={space}
          selectedDate={selectedDate}
          sharingMapId={sharingMapId}
          reservation={reservation}
        />
      </Layout>
    </>
  );
};

export default GuestReservation;
