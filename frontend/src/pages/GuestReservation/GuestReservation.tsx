import { AxiosError } from 'axios';
import { FormEventHandler, useEffect } from 'react';
import { useMutation } from 'react-query';
import { useHistory, useLocation, useParams } from 'react-router-dom';
import { postGuestReservation, putGuestReservation } from 'api/guestReservation';
import { ReactComponent as CalendarIcon } from 'assets/svg/calendar.svg';
import Button from 'components/Button/Button';
import Header from 'components/Header/Header';
import Input from 'components/Input/Input';
import Layout from 'components/Layout/Layout';
import PageHeader from 'components/PageHeader/PageHeader';
import ReservationListItem from 'components/ReservationListItem/ReservationListItem';
import MESSAGE from 'constants/message';
import REGEXP from 'constants/regexp';
import RESERVATION from 'constants/reservation';
import useGuestReservations from 'hooks/useGuestReservations';
import useInputs from 'hooks/useInputs';
import { GuestMapState } from 'pages/GuestMap/GuestMap';
import { MapItem, Reservation, ScrollPosition, Space } from 'types/common';
import { ErrorResponse } from 'types/response';
import { formatDate, formatTime, formatTimePrettier } from 'utils/datetime';
import * as Styled from './GuestReservation.styles';

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

interface Form {
  name: string;
  description: string;
  date: string;
  startTime: string;
  endTime: string;
  password: string;
}

const GuestReservation = (): JSX.Element => {
  const location = useLocation<GuestReservationState>();
  const history = useHistory<GuestMapState>();
  const { sharingMapId } = useParams<URLParameter>();

  const { mapId, space, selectedDate, scrollPosition, reservation } = location.state;
  const { availableStartTime, availableEndTime, reservationTimeUnit, reservationMaximumTimeUnit } =
    space.settings;

  if (!mapId || !space) history.replace(`/guest/${sharingMapId}`);

  const now = new Date();
  const todayDate = formatDate(new Date());

  const initialStartTime = !!reservation
    ? formatTime(new Date(reservation.startDateTime))
    : formatTime(now);
  const initialEndTime = !!reservation
    ? formatTime(new Date(reservation.endDateTime))
    : formatTime(new Date(new Date().getTime() + 1000 * 60 * reservationTimeUnit));

  const availableStartTimeText = formatTime(new Date(`${todayDate}T${availableStartTime}`));
  const availableEndTimeText = formatTime(new Date(`${todayDate}T${availableEndTime}`));

  const [{ name, description, date, startTime, endTime, password }, onChangeForm] = useInputs<Form>(
    {
      name: reservation?.name ?? '',
      description: reservation?.description ?? '',
      date: selectedDate,
      startTime: initialStartTime,
      endTime: initialEndTime,
      password: '',
    }
  );

  const startDateTime = new Date(`${date}T${startTime}Z`);
  const endDateTime = new Date(`${date}T${endTime}Z`);

  const getReservations = useGuestReservations({ mapId, spaceId: space.id, date });
  const reservations = getReservations.data?.data?.reservations ?? [];

  const createReservation = useMutation(postGuestReservation, {
    onSuccess: () => {
      history.push(`/guest/${sharingMapId}`, {
        spaceId: space.id,
        targetDate: new Date(`${date}T${startTime}`),
      });
    },
    onError: (error: AxiosError<ErrorResponse>) => {
      alert(error.response?.data.message ?? MESSAGE.RESERVATION.UNEXPECTED_ERROR);
    },
  });

  const editReservation = useMutation(putGuestReservation, {
    onSuccess: () => {
      history.push(`/guest/${sharingMapId}`, {
        spaceId: space.id,
        targetDate: new Date(`${date}T${startTime}`),
      });
    },

    onError: (error: AxiosError<ErrorResponse>) => {
      alert(error.response?.data.message ?? MESSAGE.RESERVATION.UNEXPECTED_ERROR);
    },
  });

  const handleReservationCreate: FormEventHandler<HTMLFormElement> = (event) => {
    event.preventDefault();

    if (createReservation.isLoading) return;

    createReservation.mutate({
      reservation: { name, description, password, startDateTime, endDateTime },
      mapId,
      spaceId: space.id,
    });
  };

  const handleReservationEdit: FormEventHandler<HTMLFormElement> = (event) => {
    event.preventDefault();

    if (editReservation.isLoading || !reservation) return;

    editReservation.mutate({
      reservation: { name, description, password, startDateTime, endDateTime },
      mapId,
      spaceId: space.id,
      reservationId: reservation.id,
    });
  };

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

  useEffect(() => {
    window.scrollTo(0, 0);
  }, []);

  return (
    <>
      <Header />
      <Layout>
        <Styled.ReservationForm
          onSubmit={!!reservation ? handleReservationEdit : handleReservationCreate}
        >
          <Styled.Section>
            <Styled.PageHeader title="공간 이름" data-testid="spaceName">
              <Styled.ColorDot color={space.color} />
              {space.name}
            </Styled.PageHeader>
            <Styled.InputWrapper>
              <Input
                label="이름"
                name="name"
                value={name}
                onChange={onChangeForm}
                maxLength={RESERVATION.NAME.MAX_LENGTH}
                autoFocus
                required
              />
            </Styled.InputWrapper>
            <Styled.InputWrapper>
              <Input
                label="사용 목적"
                name="description"
                value={description}
                onChange={onChangeForm}
                maxLength={RESERVATION.DESCRIPTION.MAX_LENGTH}
                required
              />
            </Styled.InputWrapper>
            <Styled.InputWrapper>
              <Input
                type="date"
                name="date"
                label="날짜"
                icon={<CalendarIcon />}
                value={date}
                min={formatDate(now)}
                onChange={onChangeForm}
                required
              />
            </Styled.InputWrapper>
            <Styled.InputWrapper>
              <Input
                type="time"
                label="시작 시간"
                name="startTime"
                step={60 * reservationTimeUnit}
                min={availableStartTime}
                max={availableEndTime}
                value={startTime}
                onChange={onChangeForm}
                required
              />
              <Input
                type="time"
                label="종료 시간"
                name="endTime"
                step={60 * reservationTimeUnit}
                min={startTime}
                max={availableEndTime}
                value={endTime}
                onChange={onChangeForm}
                required
              />
              <Styled.TimeFormMessage>
                예약 가능 시간 : {availableStartTimeText} ~ {availableEndTimeText} (최대{' '}
                {formatTimePrettier(reservationMaximumTimeUnit)})
              </Styled.TimeFormMessage>
            </Styled.InputWrapper>
            <Styled.InputWrapper>
              <Input
                type="password"
                label="비밀번호"
                name="password"
                value={password}
                onChange={onChangeForm}
                minLength={RESERVATION.PASSWORD.MIN_LENGTH}
                maxLength={RESERVATION.PASSWORD.MAX_LENGTH}
                pattern={REGEXP.RESERVATION_PASSWORD.source}
                inputMode="numeric"
                message="숫자 4자리를 입력해주세요."
                status={
                  createReservation.error?.response?.data.field === 'password' ? 'error' : 'default'
                }
                required
              />
            </Styled.InputWrapper>
          </Styled.Section>
          <Styled.Section>
            <PageHeader title={`${date}${date && '의'} 예약 목록`} />
            {getReservations.isLoadingError && (
              <Styled.Message>
                예약 목록을 불러오는 데 문제가 생겼어요!
                <br />
                새로 고침으로 다시 시도해주세요.
              </Styled.Message>
            )}
            {getReservations.isLoading && !getReservations.isLoadingError && (
              <Styled.Message>불러오는 중입니다...</Styled.Message>
            )}
            {getReservations.isSuccess && reservations.length === 0 && (
              <Styled.Message>오늘의 첫 예약을 잡아보세요!</Styled.Message>
            )}
            {getReservations.isSuccess && reservations.length > 0 && (
              <Styled.ReservationList role="list">
                {reservations?.map((reservation) => (
                  <ReservationListItem key={reservation.id} reservation={reservation} />
                ))}
              </Styled.ReservationList>
            )}
          </Styled.Section>

          <Styled.ButtonWrapper>
            <Button fullWidth variant="primary" size="large">
              {!!reservation ? '예약 수정하기' : '예약하기'}
            </Button>
          </Styled.ButtonWrapper>
        </Styled.ReservationForm>
      </Layout>
    </>
  );
};

export default GuestReservation;
