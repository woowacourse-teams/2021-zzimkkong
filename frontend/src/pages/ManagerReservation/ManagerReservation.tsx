import { AxiosError } from 'axios';
import { useEffect } from 'react';
import { useMutation } from 'react-query';
import { useHistory, useLocation } from 'react-router-dom';
import {
  postManagerReservation,
  PostReservationParams,
  putManagerReservation,
  PutReservationParams,
} from 'api/managerReservation';
import Header from 'components/Header/Header';
import Layout from 'components/Layout/Layout';
import PageHeader from 'components/PageHeader/PageHeader';
import ReservationListItem from 'components/ReservationListItem/ReservationListItem';
import DATE from 'constants/date';
import MESSAGE from 'constants/message';
import PATH from 'constants/path';
import useManagerSpaceReservations from 'hooks/query/useManagerSpaceReservations';
import useInput from 'hooks/useInput';
import { ManagerMainState } from 'pages/ManagerMain/ManagerMain';
import { ManagerSpaceAPI, Reservation } from 'types/common';
import { ErrorResponse } from 'types/response';
import { isFutureDate, isPastDay, isPastDayThanMinDay } from 'utils/datetime';
import * as Styled from './ManagerReservation.styles';
import ManagerReservationForm from './units/ManagerReservationForm';

export type EditReservationParams = Omit<PutReservationParams, 'mapId' | 'spaceId'>;
export type CreateReservationParams = Omit<PostReservationParams, 'mapId' | 'spaceId'>;

interface ManagerReservationState {
  mapId: number;
  space: ManagerSpaceAPI;
  selectedDate: string;
  reservation?: Reservation;
}

const ManagerReservation = (): JSX.Element => {
  const location = useLocation<ManagerReservationState>();
  const history = useHistory<ManagerMainState>();

  const { mapId, space, selectedDate, reservation } = location.state;

  if (!mapId || !space) history.replace(PATH.MANAGER_MAIN);

  const [date, , setDate] = useInput(selectedDate);

  const isEditMode = !!reservation;
  const isPastDate = isPastDay(new Date(date));
  const isPastDateThanMinDay = isPastDayThanMinDay(new Date(date));

  const getReservations = useManagerSpaceReservations(
    { mapId, spaceId: space.id, date },
    {
      enabled: !isPastDateThanMinDay && !!date,
    }
  );
  const reservations = getReservations.data?.data?.reservations ?? [];

  const addReservation = useMutation(postManagerReservation, {
    onSuccess: () => {
      history.push({
        pathname: PATH.MANAGER_MAIN,
        state: {
          mapId,
          targetDate: new Date(date),
        },
      });
    },
    onError: (error: AxiosError<ErrorResponse>) => {
      alert(error.response?.data.message ?? MESSAGE.RESERVATION.UNEXPECTED_ERROR);
    },
  });

  const updateReservation = useMutation(putManagerReservation, {
    onSuccess: () => {
      history.push({
        pathname: PATH.MANAGER_MAIN,
        state: {
          mapId,
          targetDate: new Date(date),
        },
      });
    },

    onError: (error: AxiosError<ErrorResponse>) => {
      alert(error.response?.data.message ?? MESSAGE.RESERVATION.UNEXPECTED_ERROR);
    },
  });

  const createReservation = ({ reservation }: CreateReservationParams) => {
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

    if (isPastDateThanMinDay) {
      setDate(DATE.MIN_DATE_STRING);
      return;
    }

    if (isFutureDate(new Date(date), DATE.MAX_DATE)) {
      setDate(DATE.MAX_DATE_STRING);
      return;
    }

    setDate(value);
  };

  useEffect(() => {
    return history.listen((location) => {
      if (
        location.pathname === PATH.MANAGER_MAIN ||
        location.pathname === PATH.MANAGER_MAIN + '/'
      ) {
        location.state = {
          mapId,
          targetDate: new Date(date),
        };
      }
    });
  }, [history, date, mapId]);

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
          onChangeDate={handleChangeDate}
          onCreateReservation={createReservation}
          onEditReservation={editReservation}
        />
        <Styled.Section isEditMode={isEditMode}>
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
          {getReservations.isSuccess && reservations.length === 0 && isPastDate && (
            <Styled.Message>{MESSAGE.RESERVATION.NOT_EXIST}</Styled.Message>
          )}
          {(isPastDateThanMinDay || isFutureDate(new Date(date), DATE.MAX_DATE)) && (
            <Styled.Message>{MESSAGE.RESERVATION.NOT_EXIST}</Styled.Message>
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
