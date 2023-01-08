import { AxiosError } from 'axios';
import dayjs from 'dayjs';
import React, { useContext, useEffect } from 'react';
import { useMutation } from 'react-query';
import { useHistory, useLocation, useParams } from 'react-router-dom';
import {
  postGuestReservation,
  putGuestReservation,
  GuestReservationParams,
  postMemberGuestReservation,
  MemberGuestReservationParams,
  putMemberGuestReservation,
} from 'api/guestReservation';
import Header from 'components/Header/Header';
import Layout from 'components/Layout/Layout';
import ManagerReservationListItem from 'components/ManagerReservationListItem/ManagerReservationListItem';
import PageHeader from 'components/PageHeader/PageHeader';
import DATE from 'constants/date';
import MESSAGE from 'constants/message';
import { HREF } from 'constants/path';
import useGuestReservations from 'hooks/query/useGuestReservations';
import useInput from 'hooks/useInput';
import { GuestMapState } from 'pages/GuestMap/GuestMap';
import { AccessTokenContext } from 'providers/AccessTokenProvider';
import { Reservation, ScrollPosition, Space } from 'types/common';
import { GuestPageURLParams } from 'types/guest';
import { ErrorResponse } from 'types/response';
import { isFutureDate, isPastDate } from 'utils/datetime';
import { getReservationStatus } from 'utils/reservation';
import * as Styled from './GuestReservation.styles';
import { GuestReservationSuccessState } from './GuestReservationSuccess';
import GuestReservationForm from './units/GuestReservationForm';
import MemberGuestReservationForm from './units/MemberGuestReservationForm';

interface GuestReservationState {
  mapId: number;
  space: Space;
  selectedDate: string;
  scrollPosition: ScrollPosition;
  reservation?: Reservation;
}

export interface EditGuestReservationParams extends GuestReservationParams {
  reservationId?: number;
}

export interface EditMemberGuestReservationParams extends MemberGuestReservationParams {
  reservationId?: number;
}

const GuestReservation = (): JSX.Element => {
  const { accessToken } = useContext(AccessTokenContext);

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

  const addGuestReservation = useMutation(postGuestReservation, {
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

  const addMemberGuestReservation = useMutation(postMemberGuestReservation, {
    onSuccess: (_, { reservation }) => {
      const { startDateTime, endDateTime, description } = reservation;

      history.push({
        pathname: HREF.GUEST_RESERVATION_SUCCESS(sharingMapId),
        state: {
          space,
          reservation: {
            startDateTime,
            endDateTime,
            name: '',
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

  const updateGuestReservation = useMutation(putGuestReservation, {
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

  const updateMemberGuestReservation = useMutation(putMemberGuestReservation, {
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

  const createGuestReservation = ({ reservation }: GuestReservationParams) => {
    if (addGuestReservation.isLoading) return;

    addGuestReservation.mutate({
      reservation,
      mapId,
      spaceId: space.id,
    });
  };

  const createMemberGuestReservation = ({ reservation }: MemberGuestReservationParams) => {
    if (addMemberGuestReservation.isLoading) return;

    addMemberGuestReservation.mutate({
      reservation,
      mapId,
      spaceId: space.id,
    });
  };

  const editGuestReservation = ({ reservation, reservationId }: EditGuestReservationParams) => {
    if (updateGuestReservation.isLoading || !isEditMode || !reservationId) return;

    updateGuestReservation.mutate({
      reservation,
      mapId,
      spaceId: space.id,
      reservationId,
    });
  };

  const editMemberGuestReservation = ({
    reservation,
    reservationId,
  }: EditMemberGuestReservationParams) => {
    if (updateGuestReservation.isLoading || !isEditMode || !reservationId) return;

    updateMemberGuestReservation.mutate({
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

  const handleSubmitGuest = (
    event: React.FormEvent<HTMLFormElement>,
    { reservation, reservationId }: EditGuestReservationParams
  ) => {
    event.preventDefault();

    if (accessToken) return;

    if (reservationId) {
      editGuestReservation({ reservation, reservationId });
    } else {
      createGuestReservation({ reservation });
    }
  };

  const handleSubmitMemberGuest = (
    event: React.FormEvent<HTMLFormElement>,
    { reservation, reservationId }: EditMemberGuestReservationParams
  ) => {
    event.preventDefault();

    if (!accessToken) return;

    if (reservationId) {
      editMemberGuestReservation({ reservation, reservationId });
    } else {
      createMemberGuestReservation({ reservation });
    }
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
          <Styled.PageHeader title="공간 이름" data-testid="spaceName">
            <Styled.ColorDot color={space.color} size="medium" />
            {space.name}
          </Styled.PageHeader>
          {accessToken ? (
            <MemberGuestReservationForm
              isEditMode={isEditMode}
              space={space}
              reservation={reservation}
              date={date}
              onChangeDate={handleChangeDate}
              onSubmit={handleSubmitMemberGuest}
            />
          ) : (
            <GuestReservationForm
              isEditMode={isEditMode}
              space={space}
              reservation={reservation}
              date={date}
              onChangeDate={handleChangeDate}
              onSubmit={handleSubmitGuest}
            />
          )}
          <Styled.Section>
            <PageHeader title={`${date}${date && '의'} 예약 목록`} />
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

export default GuestReservation;
