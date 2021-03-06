import React, { ChangeEventHandler } from 'react';
import { ReactComponent as CalendarIcon } from 'assets/svg/calendar.svg';
import Input from 'components/Input/Input';
import TimePicker, { Step } from 'components/TimePicker/TimePicker';
import DATE from 'constants/date';
import MESSAGE from 'constants/message';
import REGEXP from 'constants/regexp';
import RESERVATION from 'constants/reservation';
import useInputs from 'hooks/useInputs';
import useScrollToTop from 'hooks/useScrollToTop';
import useTimePicker from 'hooks/useTimePicker';
import { Reservation, Space } from 'types/common';
import {
  formatDate,
  formatTime,
  formatTimePrettier,
  formatTimeWithSecond,
  isPastDate,
} from 'utils/datetime';
import { EditReservationParams } from '../GuestReservation';
import * as Styled from './GuestReservationForm.styles';

interface Props {
  isEditMode: boolean;
  space: Space;
  reservation?: Reservation;
  date: string;
  onChangeDate: ChangeEventHandler<HTMLInputElement>;
  onSubmit: (
    event: React.FormEvent<HTMLFormElement>,
    { reservation, reservationId }: EditReservationParams
  ) => void;
}

interface Form {
  name: string;
  description: string;
  password: string;
}

const GuestReservationForm = ({
  isEditMode,
  space,
  date,
  reservation,
  onSubmit,
  onChangeDate,
}: Props): JSX.Element => {
  useScrollToTop();

  const { availableStartTime, availableEndTime, reservationTimeUnit, reservationMaximumTimeUnit } =
    space.settings;
  const { range, selectedTime, onClick, onChange, onCloseOptions } = useTimePicker({
    step: reservationTimeUnit as Step,
    initialStartTime: !!reservation ? new Date(reservation.startDateTime) : undefined,
    initialEndTime: !!reservation ? new Date(reservation.endDateTime) : undefined,
  });

  const todayDate = formatDate(new Date());

  const availableStartTimeText = formatTime(new Date(`${todayDate}T${availableStartTime}`));
  const availableEndTimeText = formatTime(new Date(`${todayDate}T${availableEndTime}`));

  const [{ name, description, password }, onChangeForm] = useInputs<Form>({
    name: reservation?.name ?? '',
    description: reservation?.description ?? '',
    password: '',
  });

  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    if (range.start === null || range.end === null) return;

    const startDateTime = `${date}T${formatTimeWithSecond(range.start)}${DATE.TIMEZONE_OFFSET}`;
    const endDateTime = `${date}T${formatTimeWithSecond(range.end)}${DATE.TIMEZONE_OFFSET}`;

    onSubmit(event, {
      reservation: {
        startDateTime,
        endDateTime,
        password,
        name,
        description,
      },
      reservationId: reservation?.id,
    });
  };
  return (
    <Styled.ReservationForm onSubmit={handleSubmit}>
      <Styled.Section>
        <Styled.InputWrapper>
          <Input
            label="??????"
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
            label="?????? ??????"
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
            label="??????"
            icon={<CalendarIcon />}
            value={date}
            min={DATE.MIN_DATE_STRING}
            max={DATE.MAX_DATE_STRING}
            onChange={onChangeDate}
            required
          />
        </Styled.InputWrapper>
        <Styled.InputWrapper>
          <TimePicker
            label="????????????"
            range={range}
            step={reservationTimeUnit as Step}
            selectedTime={selectedTime}
            onClick={onClick}
            onChange={onChange}
            onCloseOptions={onCloseOptions}
          />
          <Styled.TimeFormMessage>
            ?????? ?????? ?????? : {availableStartTimeText} ~ {availableEndTimeText} (??????{' '}
            {formatTimePrettier(reservationMaximumTimeUnit)})
          </Styled.TimeFormMessage>
        </Styled.InputWrapper>
        <Styled.InputWrapper>
          <Input
            type="password"
            label="????????????"
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
        <Styled.ReservationButton
          fullWidth
          variant="primary"
          size="large"
          disabled={isPastDate(new Date(date))}
        >
          {isEditMode ? MESSAGE.RESERVATION.EDIT : MESSAGE.RESERVATION.CREATE}
        </Styled.ReservationButton>
      </Styled.ButtonWrapper>
    </Styled.ReservationForm>
  );
};

export default GuestReservationForm;
