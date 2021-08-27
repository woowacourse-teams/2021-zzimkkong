import { AxiosError } from 'axios';
import { FormEventHandler, useEffect } from 'react';
import { useMutation } from 'react-query';
import { useHistory, useLocation, useParams } from 'react-router-dom';
import { postGuestReservation } from 'api/guestReservation';
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
import useInput from 'hooks/useInput';
import { GuestMapState } from 'pages/GuestMap/GuestMap';
import { MapItem, ScrollPosition, Space } from 'types/common';
import { ErrorResponse } from 'types/response';
import { formatDate, formatTime, formatTimePrettier } from 'utils/datetime';
import * as Styled from './GuestReservation.styles';

interface GuestReservationState {
  mapId: number;
  space: Space;
  selectedDate: string;
  scrollPosition: ScrollPosition;
}

interface URLParameter {
  sharingMapId: MapItem['sharingMapId'];
}

const GuestReservation = (): JSX.Element => {
  const location = useLocation<GuestReservationState>();
  const history = useHistory<GuestMapState>();
  const { sharingMapId } = useParams<URLParameter>();

  const { mapId, space, selectedDate, scrollPosition } = location.state;
  const { availableStartTime, availableEndTime, reservationTimeUnit, reservationMaximumTimeUnit } =
    space.settings;

  if (!mapId || !space) history.replace(`/guest/${sharingMapId}`);

  const now = new Date();
  const todayDate = formatDate(new Date());

  const initialStartTime = formatTime(now);
  const initialEndTime = formatTime(
    new Date(new Date().getTime() + 1000 * 60 * reservationTimeUnit)
  );
  const availableStartTimeText = formatTime(new Date(`${todayDate}T${availableStartTime}`));
  const availableEndTimeText = formatTime(new Date(`${todayDate}T${availableEndTime}`));

  const [name, onChangeName] = useInput('');
  const [description, onChangeDescription] = useInput('');
  const [date, onChangeDate] = useInput(selectedDate);
  const [startTime, onChangeStartTime] = useInput(initialStartTime);
  const [endTime, onChangeEndTime] = useInput(initialEndTime);
  const [password, onChangePassword] = useInput('');

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

  const handleSubmit: FormEventHandler<HTMLFormElement> = (event) => {
    event.preventDefault();

    if (createReservation.isLoading) return;

    const reservation = { name, description, password, startDateTime, endDateTime };

    createReservation.mutate({ reservation, mapId, spaceId: space.id });
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
        <Styled.ReservationForm onSubmit={handleSubmit}>
          <Styled.Section>
            <Styled.PageHeader>
              <Styled.ColorDot color={space.color} />
              {space.name}
            </Styled.PageHeader>
            <Styled.InputWrapper>
              <Input
                label="이름"
                value={name}
                onChange={onChangeName}
                maxLength={RESERVATION.NAME.MAX_LENGTH}
                autoFocus
                required
              />
            </Styled.InputWrapper>
            <Styled.InputWrapper>
              <Input
                label="사용 목적"
                value={description}
                onChange={onChangeDescription}
                maxLength={RESERVATION.DESCRIPTION.MAX_LENGTH}
                required
              />
            </Styled.InputWrapper>
            <Styled.InputWrapper>
              <Input
                type="date"
                label="날짜"
                icon={<CalendarIcon />}
                value={date}
                min={formatDate(now)}
                onChange={onChangeDate}
                required
              />
            </Styled.InputWrapper>
            <Styled.InputWrapper>
              <Input
                type="time"
                label="시작 시간"
                step={60 * reservationTimeUnit}
                min={availableStartTime}
                max={availableEndTime}
                value={startTime}
                onChange={onChangeStartTime}
                required
              />
              <Input
                type="time"
                label="종료 시간"
                step={60 * reservationTimeUnit}
                min={startTime}
                max={availableEndTime}
                value={endTime}
                onChange={onChangeEndTime}
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
                value={password}
                onChange={onChangePassword}
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
              예약하기
            </Button>
          </Styled.ButtonWrapper>
        </Styled.ReservationForm>
      </Layout>
    </>
  );
};

export default GuestReservation;
