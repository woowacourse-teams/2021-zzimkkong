import { AxiosError } from 'axios';
import { FormEventHandler } from 'react';
import { useMutation } from 'react-query';
import { useHistory } from 'react-router';
import { postGuestReservation, putGuestReservation } from 'api/guestReservation';
import { ReactComponent as CalendarIcon } from 'assets/svg/calendar.svg';
import Button from 'components/Button/Button';
import Input from 'components/Input/Input';
import MESSAGE from 'constants/message';
import { HREF } from 'constants/path';
import REGEXP from 'constants/regexp';
import RESERVATION from 'constants/reservation';
import useInputs from 'hooks/useInputs';
import useWindowScrollReset from 'hooks/useWindowScrollReset';
import { MapItem, Reservation, Space } from 'types/common';
import { ErrorResponse } from 'types/response';
import { formatDate, formatTime, formatTimePrettier } from 'utils/datetime';
import * as Styled from './ReservationForm.styles';

interface Props {
  mapId: number;
  space: Space;
  selectedDate: string;
  sharingMapId: MapItem['sharingMapId'];
  reservation?: Reservation;
}

interface Form {
  name: string;
  description: string;
  date: string;
  startTime: string;
  endTime: string;
  password: string;
}

const ReservationForm = ({
  mapId,
  space,
  selectedDate,
  reservation,
  sharingMapId,
}: Props): JSX.Element => {
  useWindowScrollReset();

  const history = useHistory();

  const { availableStartTime, availableEndTime, reservationTimeUnit, reservationMaximumTimeUnit } =
    space.settings;

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

  const isEditMode = !!reservation;

  const startDateTime = new Date(`${date}T${startTime}Z`);
  const endDateTime = new Date(`${date}T${endTime}Z`);

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
      history.push(HREF.GUEST_MAP(sharingMapId), {
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

  return (
    <Styled.ReservationForm onSubmit={isEditMode ? handleReservationEdit : handleReservationCreate}>
      <Styled.Section>
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
            step={RESERVATION.TIME.MINUTE_TO_SECONDS * reservationTimeUnit}
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
            step={RESERVATION.TIME.MINUTE_TO_SECONDS * reservationTimeUnit}
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
            message={MESSAGE.RESERVATION.PASSWORD_MESSAGE}
            status={
              createReservation.error?.response?.data.field === 'password' ? 'error' : 'default'
            }
            required
          />
        </Styled.InputWrapper>
      </Styled.Section>
      <Styled.ButtonWrapper>
        <Button fullWidth variant="primary" size="large">
          {isEditMode ? MESSAGE.RESERVATION.EDIT : MESSAGE.RESERVATION.CREATE}
        </Button>
      </Styled.ButtonWrapper>
    </Styled.ReservationForm>
  );
};

export default ReservationForm;
