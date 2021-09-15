import { AxiosError } from 'axios';
import React from 'react';
import { useMutation } from 'react-query';
import { useHistory, useLocation } from 'react-router-dom';
import { postGuestReservation, putGuestReservation, ReservationParams } from 'api/guestReservation';
import Header from 'components/Header/Header';
import Layout from 'components/Layout/Layout';
import PageHeader from 'components/PageHeader/PageHeader';
import ReservationListItem from 'components/ReservationListItem/ReservationListItem';
import MESSAGE from 'constants/message';
import PATH from 'constants/path';
import useGuestReservations from 'hooks/query/useGuestReservations';
import useInput from 'hooks/useInput';
import { GuestMapState } from 'pages/GuestMap/GuestMap';
import { ManagerSpaceAPI, Reservation } from 'types/common';
import { ErrorResponse } from 'types/response';
import * as Styled from './ManagerReservation.styles';
import ManagerReservationForm from './units/ManagerReservationForm';

interface ManagerReservationState {
  mapId: number;
  space: ManagerSpaceAPI;
  selectedDate: string;
  reservation?: Reservation;
}

export interface EditReservationParams extends ReservationParams {
  reservationId?: number;
}

const ManagerReservation = (): JSX.Element => {
  const location = useLocation<ManagerReservationState>();
  const history = useHistory<GuestMapState>();

  const { mapId, space, selectedDate, reservation } = location.state;

  if (!mapId || !space) history.replace(PATH.MANAGER_MAIN);

  const [date, onChangeDate] = useInput(selectedDate);

  const isEditMode = !!reservation;

  const getReservations = useGuestReservations({ mapId, spaceId: space.id, date });
  const reservations = getReservations.data?.data?.reservations ?? [];

  const addReservation = useMutation(postGuestReservation, {
    onSuccess: () => {
      history.push(PATH.MANAGER_MAIN, {
        spaceId: space.id,
        targetDate: new Date(date),
      });
    },
    onError: (error: AxiosError<ErrorResponse>) => {
      alert(error.response?.data.message ?? MESSAGE.RESERVATION.UNEXPECTED_ERROR);
    },
  });

  const updateReservation = useMutation(putGuestReservation, {
    onSuccess: () => {
      history.push(PATH.MANAGER_MAIN, {
        spaceId: space.id,
        targetDate: new Date(date),
      });
    },

    onError: (error: AxiosError<ErrorResponse>) => {
      alert(error.response?.data.message ?? MESSAGE.RESERVATION.UNEXPECTED_ERROR);
    },
  });

  const createReservation = ({ reservation }: ReservationParams) => {
    if (addReservation.isLoading) return;

    addReservation.mutate({
      reservation,
      mapId,
      spaceId: space.id,
    });
  };

  const editReservation = ({ reservation, reservationId }: EditReservationParams) => {
    if (updateReservation.isLoading || !isEditMode || !reservationId) return;

    updateReservation.mutate({
      reservation,
      mapId,
      spaceId: space.id,
      reservationId,
    });
  };

  const handleSubmit = (
    event: React.FormEvent<HTMLFormElement>,
    { reservation, reservationId }: EditReservationParams
  ) => {
    event.preventDefault();

    reservationId
      ? editReservation({ reservation, reservationId })
      : createReservation({ reservation });
  };

  return (
    <>
      <Header />
      <Layout>
        <Styled.PageHeader title="공간 이름" data-testid="spaceName">
          <Styled.ColorDot color={space.color} size="medium" />
          {space.name}
        </Styled.PageHeader>
        <ManagerReservationForm
          isEditMode={isEditMode}
          space={space}
          reservation={reservation}
          date={date}
          onChangeDate={onChangeDate}
          onSubmit={handleSubmit}
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

export default ManagerReservation;