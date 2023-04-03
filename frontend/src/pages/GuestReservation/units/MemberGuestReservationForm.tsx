import dayjs from 'dayjs';
import React, { ChangeEventHandler, useMemo } from 'react';
import { ReactComponent as CalendarIcon } from 'assets/svg/calendar.svg';
import Input from 'components/Input/Input';
import TimePicker, { Step } from 'components/TimePicker/TimePicker';
import DATE from 'constants/date';
import MESSAGE from 'constants/message';
import RESERVATION from 'constants/reservation';
import SPACE from 'constants/space';
import useInputs from 'hooks/useInputs';
import useScrollToTop from 'hooks/useScrollToTop';
import useTimePicker from 'hooks/useTimePicker';
import { Reservation, Space } from 'types/common';
import {
  convertSettingTimeToMinutes,
  convertTimeToMinutes,
  formatTimeWithSecond,
  isPastDate,
} from 'utils/datetime';
import useSettingSummary from '../../../hooks/query/useSettingSummary';
import { EditMemberGuestReservationParams } from '../GuestReservation';
import * as Styled from './GuestReservationForm.styles';

interface Props {
  isEditMode: boolean;
  space: Pick<Space, 'settings'>;
  spaceId: number;
  mapId: number;
  reservation?: Reservation;
  date: string;
  userName: string;
  onChangeDate: ChangeEventHandler<HTMLInputElement>;
  onSubmit: (
    event: React.FormEvent<HTMLFormElement>,
    { reservation, reservationId }: EditMemberGuestReservationParams
  ) => void;
}

interface Form {
  description: string;
}

const MemberGuestReservationForm = ({
  isEditMode,
  mapId,
  spaceId,
  space,
  date,
  reservation,
  userName,
  onSubmit,
  onChangeDate,
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
      spaceId,
      selectedDateTime: `${date}T${formatTimeWithSecond(range.start ?? dayjs().tz())}${
        DATE.TIMEZONE_OFFSET
      }`,
    },
    {}
  );
  const settingsSummary = getSettingsSummary.data?.data?.summary ?? '';

  const [{ description }, onChangeForm] = useInputs<Form>({
    description: reservation?.description ?? '',
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
        description,
      },
      reservationId: reservation?.id,
    });
  };
  return (
    <Styled.ReservationForm onSubmit={handleSubmit}>
      <Styled.Section>
        <Styled.InputWrapper>
          <Input label="이름" value={userName} disabled />
        </Styled.InputWrapper>
        <Styled.InputWrapper>
          <Input
            label="사용 목적"
            name="description"
            value={description}
            onChange={onChangeForm}
            maxLength={RESERVATION.DESCRIPTION.MAX_LENGTH}
            required
            autoFocus
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
            label="예약시간"
            range={range}
            step={reservationTimeStep as Step}
            selectedTime={selectedTime}
            onClick={onClick}
            onChange={onChange}
            onCloseOptions={onCloseOptions}
          />
          <Styled.TimeFormMessageWrapper>
            <Styled.TimeFormMessage fontWeight="bold">예약 가능 시간</Styled.TimeFormMessage>
            <Styled.TimeFormMessage>{settingsSummary}</Styled.TimeFormMessage>
          </Styled.TimeFormMessageWrapper>
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

export default MemberGuestReservationForm;
