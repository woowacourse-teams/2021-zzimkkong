import { FormEvent } from 'react';
import { ReservationParams } from 'api/guestReservation';
import { ReactComponent as CalendarIcon } from 'assets/svg/calendar.svg';
import Button from 'components/Button/Button';
import Input from 'components/Input/Input';
import MESSAGE from 'constants/message';
import REGEXP from 'constants/regexp';
import RESERVATION from 'constants/reservation';
import TIME from 'constants/time';
import useInputs from 'hooks/useInputs';
import useWindowScrollReset from 'hooks/useWindowScrollReset';
import { Reservation, Space } from 'types/common';
import { formatDate, formatTime, formatTimePrettier } from 'utils/datetime';
import { HandleSubmitParams } from './GuestReservation';
import * as Styled from './ReservationForm.styles';

interface Props {
  space: Space;
  selectedDate: string;
  reservation?: Reservation;
  handleSubmit: ({ event, reservation, reservationId }: HandleSubmitParams) => void;
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
  space,
  selectedDate,
  reservation,
  handleSubmit,
}: Props): JSX.Element => {
  useWindowScrollReset();

  const { availableStartTime, availableEndTime, reservationTimeUnit, reservationMaximumTimeUnit } =
    space.settings;

  const isEditMode = !!reservation;

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

  return (
    <Styled.ReservationForm
      onSubmit={(event) =>
        handleSubmit({
          event,
          reservation: {
            startDateTime,
            endDateTime,
            password,
            name,
            description,
          },
          reservationId: reservation?.id,
        })
      }
    >
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
            step={TIME.SECONDS_PER_MINUTE * reservationTimeUnit}
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
            step={TIME.SECONDS_PER_MINUTE * reservationTimeUnit}
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
