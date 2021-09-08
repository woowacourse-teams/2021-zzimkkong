import { AxiosError } from 'axios';
import React, { useEffect } from 'react';
import { useMutation } from 'react-query';
import { useHistory, useLocation, useParams } from 'react-router-dom';
import { postGuestReservation, putGuestReservation, ReservationParams } from 'api/guestReservation';
import Header from 'components/Header/Header';
import Layout from 'components/Layout/Layout';
import PageHeader from 'components/PageHeader/PageHeader';
import ReservationListItem from 'components/ReservationListItem/ReservationListItem';
import MESSAGE from 'constants/message';
import { HREF } from 'constants/path';
import useGuestReservations from 'hooks/useGuestReservations';
import useInput from 'hooks/useInput';
import { GuestMapState } from 'pages/GuestMap/GuestMap';
import { MapItem, Reservation, ScrollPosition, Space } from 'types/common';
import { ErrorResponse } from 'types/response';
import * as Styled from './GuestReservation.styles';
import ReservationForm from './ReservationForm';

interface URLParameter {
  sharingMapId: MapItem['sharingMapId'];
}
interface GuestReservationState {
  mapId: number;
  space: Space;
  selectedDate: string;
  scrollPosition: ScrollPosition;
  reservation?: Reservation;
}

interface EditReservationParams extends ReservationParams {
  reservationId: number;
}

export interface HandleSubmitParams extends ReservationParams {
  event: React.FormEvent<HTMLFormElement>;
  reservationId?: number;
}

const GuestReservation = (): JSX.Element => {
  const location = useLocation<GuestReservationState>();
  const history = useHistory<GuestMapState>();
  const { sharingMapId } = useParams<URLParameter>();

  const { mapId, space, selectedDate, scrollPosition, reservation } = location.state;

  if (!mapId || !space) history.replace(HREF.GUEST_MAP(sharingMapId));

  const [date, onChangeDate] = useInput(selectedDate);

  const getReservations = useGuestReservations({ mapId, spaceId: space.id, date });
  const reservations = getReservations.data?.data?.reservations ?? [];

  const createReservation = useMutation(postGuestReservation, {
    onSuccess: () => {
      history.push(HREF.GUEST_MAP(sharingMapId), {
        spaceId: space.id,
        targetDate: new Date(date),
      });
    },
    onError: (error: AxiosError<ErrorResponse>) => {
      alert(error.response?.data.message ?? MESSAGE.RESERVATION.UNEXPECTED_ERROR);
    },
  });

  const editReservation = useMutation(putGuestReservation, {
    onSuccess: () => {
      history.push(HREF.GUEST_MAP(sharingMapId), {
        spaceId: space.id,
        targetDate: new Date(date),
      });
    },

    onError: (error: AxiosError<ErrorResponse>) => {
      alert(error.response?.data.message ?? MESSAGE.RESERVATION.UNEXPECTED_ERROR);
    },
  });

  const handleReservationCreate = ({ reservation }: ReservationParams) => {
    if (createReservation.isLoading) return;

    createReservation.mutate({
      reservation,
      mapId,
      spaceId: space.id,
    });
  };

  const handleReservationEdit = ({ reservation, reservationId }: EditReservationParams) => {
    if (editReservation.isLoading || !reservation) return;

    editReservation.mutate({
      reservation,
      mapId,
      spaceId: space.id,
      reservationId,
    });
  };

  const handleSubmit = ({ event, reservation, reservationId }: HandleSubmitParams) => {
    event.preventDefault();

    reservationId
      ? handleReservationEdit({ reservation, reservationId })
      : handleReservationCreate({ reservation });
  };

  useEffect(() => {
    return history.listen((location) => {
      if (
        location.pathname === HREF.GUEST_MAP(sharingMapId) ||
        location.pathname === HREF.GUEST_MAP(sharingMapId) + '/'
      ) {
        location.state = {
          spaceId: space.id,
          targetDate: new Date(date),
          scrollPosition,
        };
      }
    });
  }, [history, scrollPosition, date, space, sharingMapId]);

  return (
    <>
      <Header />
      <Layout>
        <Styled.PageHeader title="공간 이름" data-testid="spaceName">
          <Styled.ColorDot color={space.color} />
          {space.name}
        </Styled.PageHeader>
        <ReservationForm
          space={space}
          reservation={reservation}
          date={date}
          onChangeDate={onChangeDate}
          handleSubmit={handleSubmit}
        />
        <Styled.Section>
          <PageHeader title={`${date}${date && '의'} 예약 목록`} />
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
