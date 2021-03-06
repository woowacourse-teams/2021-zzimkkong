import { AxiosError } from 'axios';
import dayjs from 'dayjs';
import React, { useEffect } from 'react';
import { useMutation } from 'react-query';
import { useHistory, useLocation, useParams } from 'react-router-dom';
import { postGuestReservation, putGuestReservation, ReservationParams } from 'api/guestReservation';
import Header from 'components/Header/Header';
import Layout from 'components/Layout/Layout';
import PageHeader from 'components/PageHeader/PageHeader';
import ReservationListItem from 'components/ReservationListItem/ReservationListItem';
import DATE from 'constants/date';
import MESSAGE from 'constants/message';
import { HREF } from 'constants/path';
import useGuestReservations from 'hooks/query/useGuestReservations';
import useInput from 'hooks/useInput';
import { GuestMapState } from 'pages/GuestMap/GuestMap';
import { Reservation, ScrollPosition, Space } from 'types/common';
import { GuestPageURLParams } from 'types/guest';
import { ErrorResponse } from 'types/response';
import { isFutureDate, isPastDate } from 'utils/datetime';
import { getReservationStatus } from 'utils/reservation';
import * as Styled from './GuestReservation.styles';
import { GuestReservationSuccessState } from './GuestReservationSuccess';
import GuestReservationForm from './units/GuestReservationForm';

interface GuestReservationState {
  mapId: number;
  space: Space;
  selectedDate: string;
  scrollPosition: ScrollPosition;
  reservation?: Reservation;
}

export interface EditReservationParams extends ReservationParams {
  reservationId?: number;
}

const GuestReservation = (): JSX.Element => {
  const location = useLocation<GuestReservationState>();
  const history = useHistory<GuestReservationSuccessState | GuestMapState>();
  const { sharingMapId } = useParams<GuestPageURLParams>();

  if (!location.state?.mapId || !location.state?.space) {
    history.replace(HREF.GUEST_MAP(sharingMapId));
  }

  const { mapId, space, selectedDate, scrollPosition, reservation } = location.state;
  const [date, , setDate] = useInput(selectedDate);

  const isEditMode = !!reservation;

  const getReservations = useGuestReservations(
    { mapId, spaceId: space.id, date },
    {
      enabled: !isPastDate(dayjs(date).tz(), DATE.MIN_DATE) && !!date,
    }
  );
  const reservations = getReservations.data?.data?.reservations ?? [];

  const addReservation = useMutation(postGuestReservation, {
    onSuccess: (_, { reservation }) => {
      const { startDateTime, endDateTime, name, description } = reservation;

      history.push({
        pathname: HREF.GUEST_RESERVATION_SUCCESS(sharingMapId),
        state: {
          space,
          reservation: {
            startDateTime,
            endDateTime,
            name,
            description,
          },
          targetDate: dayjs(date).tz(),
        },
      });
    },
    onError: (error: AxiosError<ErrorResponse>) => {
      alert(error.response?.data.message ?? MESSAGE.RESERVATION.UNEXPECTED_ERROR);
    },
  });

  const updateReservation = useMutation(putGuestReservation, {
    onSuccess: () => {
      history.push({
        pathname: HREF.GUEST_MAP(sharingMapId),
        state: {
          spaceId: space.id,
          targetDate: dayjs(date).tz(),
        },
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

  const handleChangeDate = (event: React.ChangeEvent<HTMLInputElement>) => {
    const {
      target: { value },
    } = event;

    if (isPastDate(dayjs(date).tz(), DATE.MIN_DATE)) {
      setDate(DATE.MIN_DATE_STRING);
      return;
    }

    if (isFutureDate(dayjs(date).tz(), DATE.MAX_DATE)) {
      setDate(DATE.MAX_DATE_STRING);
      return;
    }

    setDate(value);
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

  useEffect(() => {
    return history.listen((location) => {
      if (
        location.pathname === HREF.GUEST_MAP(sharingMapId) ||
        location.pathname === HREF.GUEST_MAP(sharingMapId) + '/'
      ) {
        location.state = {
          spaceId: space.id,
          targetDate: dayjs(date).tz(),
          scrollPosition,
        };
      }
    });
  }, [history, scrollPosition, date, space, sharingMapId]);

  return (
    <>
      <Header />
      <Layout>
        <Styled.Container>
          <Styled.PageHeader title="?????? ??????" data-testid="spaceName">
            <Styled.ColorDot color={space.color} size="medium" />
            {space.name}
          </Styled.PageHeader>
          <GuestReservationForm
            isEditMode={isEditMode}
            space={space}
            reservation={reservation}
            date={date}
            onChangeDate={handleChangeDate}
            onSubmit={handleSubmit}
          />
          <Styled.Section>
            <PageHeader title={`${date}${date && '???'} ?????? ??????`} />
            {getReservations.isLoadingError && (
              <Styled.Message>{MESSAGE.RESERVATION.ERROR}</Styled.Message>
            )}
            {getReservations.isLoading && !getReservations.isLoadingError && (
              <Styled.Message>{MESSAGE.RESERVATION.PENDING}</Styled.Message>
            )}
            {getReservations.isSuccess &&
              reservations.length === 0 &&
              !isPastDate(dayjs(date).tz()) && (
                <Styled.Message>{MESSAGE.RESERVATION.SUGGESTION}</Styled.Message>
              )}
            {getReservations.isSuccess &&
              reservations.length === 0 &&
              isPastDate(dayjs(date).tz()) && (
                <Styled.Message>{MESSAGE.RESERVATION.NOT_EXIST}</Styled.Message>
              )}
            {(isPastDate(dayjs(date).tz(), DATE.MIN_DATE) ||
              isFutureDate(dayjs(date).tz(), DATE.MAX_DATE)) && (
              <Styled.Message>{MESSAGE.RESERVATION.NOT_EXIST}</Styled.Message>
            )}
            {getReservations.isSuccess && reservations.length > 0 && (
              <Styled.ReservationList role="list">
                {reservations?.map((reservation) => (
                  <ReservationListItem
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

export default GuestReservation;
