import dayjs from 'dayjs';
import React, { ChangeEventHandler, useMemo } from 'react';
import { ReactComponent as CalendarIcon } from 'assets/svg/calendar.svg';
import Button from 'components/Button/Button';
import Input from 'components/Input/Input';
import TimePicker, { Step } from 'components/TimePicker/TimePicker';
import DATE from 'constants/date';
import MESSAGE from 'constants/message';
import REGEXP from 'constants/regexp';
import RESERVATION from 'constants/reservation';
import SPACE from 'constants/space';
import useInputs from 'hooks/useInputs';
import useScrollToTop from 'hooks/useScrollToTop';
import useTimePicker from 'hooks/useTimePicker';
import { ManagerSpaceAPI, Reservation } from 'types/common';
import {
  convertSettingTimeToMinutes,
  convertTimeToMinutes,
  formatTimeWithSecond,
} from 'utils/datetime';
import useSettingSummary from '../../../hooks/query/useSettingSummary';
import { CreateReservationParams, EditReservationParams } from '../ManagerReservation';
import * as Styled from './ManagerReservationForm.styles';

interface Props {
  isEditMode: boolean;
  mapId: number;
  space: ManagerSpaceAPI;
  reservation?: Reservation;
  date: string;
  onChangeDate: ChangeEventHandler<HTMLInputElement>;
  onCreateReservation: ({ reservation }: CreateReservationParams) => void;
  onEditReservation: ({ reservation, reservationId }: EditReservationParams) => void;
}

interface Form {
  name: string;
  description: string;
  password: string;
}

const ManagerReservationForm = ({
  isEditMode,
  mapId,
  space,
  date,
  reservation,
  onChangeDate,
  onCreateReservation,
  onEditReservation,
}: Props): JSX.Element => {
  useScrollToTop();

  const reservationTimeStep = useMemo(() => {
    const startTime = convertTimeToMinutes(
      reservation ? new Date(reservation.startDateTime) : new Date()
    );
    const endTime = convertTimeToMinutes(
      reservation ? new Date(reservation.endDateTime) : new Date()
    );

    return Math.min(
      ...space.settings
        .filter((setting) => {
          const settingStartTime = convertSettingTimeToMinutes(setting.settingStartTime);
          const settingEndTime = convertSettingTimeToMinutes(setting.settingEndTime);

          return (
            (settingStartTime < startTime && settingEndTime < startTime) ||
            (settingStartTime < endTime && settingEndTime > endTime)
          );
        })
        .map(({ reservationTimeUnit }) => reservationTimeUnit),
      SPACE.RESERVATION.MIN_STEP
    );
  }, [reservation, space.settings]);

  const { range, selectedTime, onClick, onChange, onCloseOptions } = useTimePicker({
    step: reservationTimeStep as Step,
    initialStartTime: !!reservation ? new Date(reservation.startDateTime) : undefined,
    initialEndTime: !!reservation ? new Date(reservation.endDateTime) : undefined,
  });

  const getSettingsSummary = useSettingSummary(
    {
      mapId,
      spaceId: space?.id,
      selectedDateTime: `${date}T${formatTimeWithSecond(range.start ?? dayjs().tz())}${
        DATE.TIMEZONE_OFFSET
      }`,
    },
    {}
  );
  const settingsSummary = getSettingsSummary.data?.data?.summary ?? '';

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

    if (!reservation) {
      onCreateReservation({
        reservation: {
          startDateTime,
          endDateTime,
          password,
          name,
          description,
        },
      });

      return;
    }

    onEditReservation({
      reservation: {
        startDateTime,
        endDateTime,
        name,
        description,
      },
      reservationId: reservation?.id,
    });
  };

  return (
    <Styled.ReservationForm onSubmit={(event) => handleSubmit(event)}>
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
            min={DATE.MIN_DATE_STRING}
            max={DATE.MAX_DATE_STRING}
            onChange={onChangeDate}
            required
          />
        </Styled.InputWrapper>
        <Styled.InputWrapper>
          <TimePicker
            label="예약 시간"
            selectedTime={selectedTime}
            range={range}
            step={reservationTimeStep as Step}
            onClick={onClick}
            onChange={onChange}
            onCloseOptions={onCloseOptions}
          />

          <Styled.TimeFormMessageWrapper>
            <Styled.TimeFormMessage fontWeight="bold">예약 가능 시간</Styled.TimeFormMessage>
            <Styled.TimeFormMessage>{settingsSummary}</Styled.TimeFormMessage>
          </Styled.TimeFormMessageWrapper>
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
