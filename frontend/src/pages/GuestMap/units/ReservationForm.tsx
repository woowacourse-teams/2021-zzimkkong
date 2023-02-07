import { AxiosError } from 'axios';
import dayjs from 'dayjs';
import React, { useContext, useMemo, useState } from 'react';
import { useMutation } from 'react-query';
import { useHistory } from 'react-router-dom';
import {
  postMemberGuestReservation,
  postGuestReservation,
  PostGuestReservationParams,
  PostMemberGuestReservationParams,
} from 'api/guestReservation';
import { ReactComponent as CalendarIcon } from 'assets/svg/calendar.svg';
import ColorDot from 'components/ColorDot/ColorDot';
import Input from 'components/Input/Input';
import Select from 'components/Select/Select';
import TimePicker from 'components/TimePicker/TimePicker';
import DATE from 'constants/date';
import MESSAGE from 'constants/message';
import { HREF } from 'constants/path';
import RESERVATION from 'constants/reservation';
import SPACE from 'constants/space';
import useGuestSpaceAvailable from 'hooks/query/useGuestSpaceAvailable';
import useGuestSpaces from 'hooks/query/useGuestSpaces';
import useMember from 'hooks/query/useMember';
import useInputs from 'hooks/useInputs';
import useTimePicker from 'hooks/useTimePicker';
import { AccessTokenContext } from 'providers/AccessTokenProvider';
import { MapItem, Space } from 'types/common';
import { ErrorResponse, SpaceResponse } from 'types/response';
import { Time } from 'types/time';
import { formatDate, formatTimePrettier, formatTimeWithSecond, isPastDate } from 'utils/datetime';
import { isNullish } from 'utils/type';
import * as Styled from './ReservationForm.styled';

interface Props {
  map: MapItem;
}

interface Form {
  description: string;
  password: string;
  date: string;
  name: string;
}

const converTimeToDate = (date: string, time: Time | null) => {
  if (time === null) {
    return null;
  }

  return `${date}T${formatTimeWithSecond(time)}${DATE.TIMEZONE_OFFSET}`;
};

const ReservationForm = ({ map }: Props) => {
  const history = useHistory();

  const { accessToken } = useContext(AccessTokenContext);
  const member = useMember({
    enabled: !!accessToken,
  });
  const userName = member.data?.data.userName;

  const [selectedSpaceId, setSelectedSpaceId] = useState('');
  const [values, onChangeValues] = useInputs<Form>({
    description: '',
    password: '',
    date: formatDate(new Date()),
    name: '',
  });

  const { range, selectedTime, onClick, onChange, onCloseOptions } = useTimePicker({
    step: SPACE.RESERVATION.MIN_STEP,
    initialStartTime: undefined,
    initialEndTime: undefined,
  });

  const getSpaces = useGuestSpaces({ mapId: map.mapId });
  const spacesMap = useMemo(() => {
    if (!getSpaces.data?.data) {
      return {};
    }

    return getSpaces.data.data.spaces.reduce((prev, cur) => {
      prev[cur.id] = cur;
      return prev;
    }, {} as Record<Space['id'], SpaceResponse>);
  }, [getSpaces.data?.data]);

  const getSpaceAvailable = useGuestSpaceAvailable(
    {
      mapId: map.mapId,
      startDateTime: converTimeToDate(values.date, range.start)!,
      endDateTime: converTimeToDate(values.date, range.end)!,
    },
    { enabled: !!range.start && !!range.end }
  );

  const getSpaceOptions = () => {
    const spaces =
      getSpaceAvailable.data?.data.spaces
        .filter((space) => space.isAvailable)
        .map((space) => spacesMap[space.spaceId]) ?? [];

    return spaces.map((space) => ({
      value: `${space.id}`,
      children: (
        <Styled.SpaceOption>
          <ColorDot size="medium" color={space.color} />
          {space.name}
        </Styled.SpaceOption>
      ),
    }));
  };

  const onSuccessCreateReservation = (
    _: any,
    { reservation }: PostGuestReservationParams | PostMemberGuestReservationParams
  ) => {
    const { startDateTime, endDateTime, description } = reservation;

    history.push({
      pathname: HREF.GUEST_RESERVATION_SUCCESS(map.sharingMapId),
      state: {
        space: spacesMap[Number(selectedSpaceId)],
        reservation: {
          startDateTime,
          endDateTime,
          name: userName ?? '',
          description,
        },
        targetDate: dayjs(values.date).tz(),
      },
    });
  };

  const addMemberGuestReservation = useMutation(postMemberGuestReservation, {
    onSuccess: onSuccessCreateReservation,
    onError: (error: AxiosError<ErrorResponse>) => {
      alert(error.response?.data.message ?? MESSAGE.RESERVATION.UNEXPECTED_ERROR);
    },
  });

  const addGuestReservation = useMutation(postGuestReservation, {
    onSuccess: onSuccessCreateReservation,
    onError: (error: AxiosError<ErrorResponse>) => {
      alert(error.response?.data.message ?? MESSAGE.RESERVATION.UNEXPECTED_ERROR);
    },
  });

  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    if (range.start === null || range.end === null) return;

    const startDateTime = `${values.date}T${formatTimeWithSecond(range.start)}${
      DATE.TIMEZONE_OFFSET
    }`;
    const endDateTime = `${values.date}T${formatTimeWithSecond(range.end)}${DATE.TIMEZONE_OFFSET}`;

    accessToken
      ? addMemberGuestReservation.mutate({
          reservation: {
            startDateTime,
            endDateTime,
            description: values.description,
          },
          mapId: map.mapId,
          spaceId: Number(selectedSpaceId),
        })
      : addGuestReservation.mutate({
          reservation: {
            startDateTime,
            endDateTime,
            description: values.description,
            name: values.name,
            password: values.password,
          },
          mapId: map.mapId,
          spaceId: Number(selectedSpaceId),
        });
  };

  return (
    <Styled.ReservationForm onSubmit={handleSubmit}>
      <Styled.Section>
        <Styled.InputWrapper>
          <Input
            type="date"
            name="date"
            label="날짜"
            icon={<CalendarIcon />}
            value={values.date}
            min={DATE.MIN_DATE_STRING}
            max={DATE.MAX_DATE_STRING}
            onChange={onChangeValues}
            required
          />
        </Styled.InputWrapper>
        <Styled.InputWrapper>
          <TimePicker
            label="예약시간"
            range={range}
            step={SPACE.RESERVATION.MIN_STEP}
            selectedTime={selectedTime}
            onClick={onClick}
            onChange={onChange}
            onCloseOptions={onCloseOptions}
          />
          {spacesMap[Number(selectedSpaceId)] && (
            <Styled.TimeFormMessageWrapper>
              <Styled.TimeFormMessage>예약 가능 시간</Styled.TimeFormMessage>
              {spacesMap[Number(selectedSpaceId)].settings.map(
                (
                  {
                    settingStartTime,
                    settingEndTime,
                    reservationMaximumTimeUnit,
                    reservationMinimumTimeUnit,
                  },
                  index
                ) => (
                  <Styled.TimeFormMessage key={index}>
                    {settingStartTime.slice(0, 5)} ~ {settingEndTime.slice(0, 5)}
                    (최소 {formatTimePrettier(reservationMinimumTimeUnit)}, 최대{' '}
                    {formatTimePrettier(reservationMaximumTimeUnit)})
                  </Styled.TimeFormMessage>
                )
              )}
            </Styled.TimeFormMessageWrapper>
          )}
        </Styled.InputWrapper>

        <Styled.InputWrapper>
          <Select
            name="space"
            label="공간 선택"
            options={getSpaceOptions()}
            value={selectedSpaceId}
            onChange={(id) => setSelectedSpaceId(id)}
          />
        </Styled.InputWrapper>
        <Styled.InputsRow>
          <Styled.InputWrapper>
            <Input
              label="이름"
              name="name"
              value={userName ?? values.name}
              onChange={onChangeValues}
              disabled={!isNullish(accessToken)}
            />
          </Styled.InputWrapper>
          {accessToken == null && (
            <Styled.InputWrapper>
              <Input
                label="비밀번호"
                type="password"
                name="password"
                value={values.password}
                onChange={onChangeValues}
              />
            </Styled.InputWrapper>
          )}
        </Styled.InputsRow>
        <Styled.InputWrapper>
          <Input
            label="사용 목적"
            name="description"
            value={values.description}
            onChange={onChangeValues}
            maxLength={RESERVATION.DESCRIPTION.MAX_LENGTH}
            required
          />
        </Styled.InputWrapper>
      </Styled.Section>

      <Styled.ButtonWrapper>
        <Styled.ReservationButton
          fullWidth
          variant="primary"
          size="large"
          disabled={isPastDate(new Date(values.date))}
        >
          {MESSAGE.RESERVATION.CREATE}
        </Styled.ReservationButton>
      </Styled.ButtonWrapper>
    </Styled.ReservationForm>
  );
};

export default ReservationForm;
