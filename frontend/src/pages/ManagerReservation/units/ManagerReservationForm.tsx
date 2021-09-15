import { ChangeEventHandler } from 'react';
import { ReactComponent as CalendarIcon } from 'assets/svg/calendar.svg';
import Button from 'components/Button/Button';
import Input from 'components/Input/Input';
import MESSAGE from 'constants/message';
import REGEXP from 'constants/regexp';
import RESERVATION from 'constants/reservation';
import TIME from 'constants/time';
import useInputs from 'hooks/useInputs';
import useScrollToTop from 'hooks/useScrollToTop';
import { ManagerSpaceAPI, Reservation } from 'types/common';
import { formatDate, formatTime, formatTimePrettier } from 'utils/datetime';
import { EditReservationParams } from '../ManagerReservation';
import * as Styled from './ManagerReservationForm.styles';

interface Props {
  isEditMode: boolean;
  space: ManagerSpaceAPI;
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
  startTime: string;
  endTime: string;
  password: string;
}

const ManagerReservationForm = ({
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

  const now = new Date();
  const todayDate = formatDate(new Date());

  const getInitialStartTime = () => {
    if (isEditMode && reservation) {
      return formatTime(new Date(reservation.startDateTime));
    }

    return formatTime(now);
  };

  const getInitialEndTime = () => {
    if (isEditMode && reservation) {
      return formatTime(new Date(reservation.endDateTime));
    }

    return formatTime(
      new Date(new Date().getTime() + TIME.MILLISECONDS_PER_MINUTE * reservationTimeUnit)
    );
  };

  const initialStartTime = getInitialStartTime();
  const initialEndTime = getInitialEndTime();

  const availableStartTimeText = formatTime(new Date(`${todayDate}T${availableStartTime}`));
  const availableEndTimeText = formatTime(new Date(`${todayDate}T${availableEndTime}`));

  const [{ name, description, startTime, endTime, password }, onChangeForm] = useInputs<Form>({
    name: reservation?.name ?? '',
    description: reservation?.description ?? '',
    startTime: initialStartTime,
    endTime: initialEndTime,
    password: '',
  });

  const startDateTime = new Date(`${date}T${startTime}Z`);
  const endDateTime = new Date(`${date}T${endTime}Z`);

  return (
    <Styled.ReservationForm
      onSubmit={(event) =>
        onSubmit(event, {
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
            onChange={onChangeDate}
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
        {isEditMode || (
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
        )}
      </Styled.Section>
      <Styled.ButtonWrapper>
        <Button fullWidth variant="primary" size="large">
          {isEditMode ? MESSAGE.RESERVATION.EDIT : MESSAGE.RESERVATION.CREATE}
        </Button>
      </Styled.ButtonWrapper>
    </Styled.ReservationForm>
  );
};

export default ManagerReservationForm;
