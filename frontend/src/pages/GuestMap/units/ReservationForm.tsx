import { AxiosError } from 'axios';
import dayjs from 'dayjs';
import React, { useContext } from 'react';
import { useMutation } from 'react-query';
import { useHistory } from 'react-router-dom';
import {
  postGuestReservation,
  PostGuestReservationParams,
  postMemberGuestReservation,
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
import useMember from 'hooks/query/useMember';
import { AccessTokenContext } from 'providers/AccessTokenProvider';
import { MapItem } from 'types/common';
import { ErrorResponse } from 'types/response';
import { formatTimePrettier, formatTimeWithSecond, isPastDate } from 'utils/datetime';
import { isNullish } from 'utils/type';
import { GuestMapFormContext } from '../providers/GuestMapFormProvider';
import * as Styled from './ReservationForm.styled';

interface Props {
  map: MapItem;
}

const ReservationForm = ({ map }: Props) => {
  const history = useHistory();

  const { accessToken } = useContext(AccessTokenContext);
  const {
    formValues,
    onChangeFormValues,
    selectedSpaceId,
    setSelectedSpaceId,
    timePicker,
    availableSpaceList,
    spacesMap,
  } = useContext(GuestMapFormContext);
  const member = useMember({
    enabled: !!accessToken,
  });
  const userName = member.data?.data.userName;

  const getSpaceOptions = () => {
    return (
      availableSpaceList?.map((space) => ({
        value: `${space.id}`,
        children: (
          <Styled.SpaceOption>
            <ColorDot size="medium" color={space.color} />
            {space.name}
          </Styled.SpaceOption>
        ),
      })) ?? []
    );
  };

  const onSuccessCreateReservation = (
    _: unknown,
    { reservation }: PostGuestReservationParams | PostMemberGuestReservationParams
  ) => {
    if (!spacesMap?.[Number(selectedSpaceId)]) return;

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
        targetDate: dayjs(formValues.date).tz(),
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

    if (timePicker?.range.start == null || timePicker?.range.end == null) return;

    const startDateTime = `${formValues.date}T${formatTimeWithSecond(timePicker.range.start)}${
      DATE.TIMEZONE_OFFSET
    }`;
    const endDateTime = `${formValues.date}T${formatTimeWithSecond(timePicker.range.end)}${
      DATE.TIMEZONE_OFFSET
    }`;

    accessToken
      ? addMemberGuestReservation.mutate({
          reservation: {
            startDateTime,
            endDateTime,
            description: formValues.description,
          },
          mapId: map.mapId,
          spaceId: Number(selectedSpaceId),
        })
      : addGuestReservation.mutate({
          reservation: {
            startDateTime,
            endDateTime,
            description: formValues.description,
            name: formValues.name,
            password: formValues.password,
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
            value={formValues.date}
            min={DATE.MIN_DATE_STRING}
            max={DATE.MAX_DATE_STRING}
            onChange={onChangeFormValues}
            required
          />
        </Styled.InputWrapper>
        <Styled.InputWrapper>
          {timePicker && (
            <TimePicker
              label="예약시간"
              range={timePicker.range}
              step={SPACE.RESERVATION.MIN_STEP}
              selectedTime={timePicker.selectedTime}
              onClick={timePicker.onClick}
              onChange={timePicker.onChange}
              onCloseOptions={timePicker.onCloseOptions}
            />
          )}
          {spacesMap?.[Number(selectedSpaceId)] && (
            <Styled.TimeFormMessageWrapper>
              <Styled.TimeFormMessage fontWeight="bold">예약 가능 시간</Styled.TimeFormMessage>
              <Styled.TimeFormMessageList>
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
                )}{' '}
              </Styled.TimeFormMessageList>
            </Styled.TimeFormMessageWrapper>
          )}
        </Styled.InputWrapper>

        <Styled.InputWrapper>
          <Select
            name="space"
            label="공간 선택"
            options={getSpaceOptions()}
            value={selectedSpaceId}
            onChange={(id) => setSelectedSpaceId?.(id)}
          />
        </Styled.InputWrapper>
        <Styled.InputsRow>
          <Styled.InputWrapper>
            <Input
              label="이름"
              name="name"
              value={userName ?? formValues.name}
              onChange={onChangeFormValues}
              disabled={!isNullish(accessToken)}
            />
          </Styled.InputWrapper>
          {accessToken == null && (
            <Styled.InputWrapper>
              <Input
                label="비밀번호"
                type="password"
                name="password"
                value={formValues.password}
                onChange={onChangeFormValues}
              />
            </Styled.InputWrapper>
          )}
        </Styled.InputsRow>
        <Styled.InputWrapper>
          <Input
            label="사용 목적"
            name="description"
            value={formValues.description}
            onChange={onChangeFormValues}
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
          disabled={isPastDate(new Date(formValues.date))}
        >
          {MESSAGE.RESERVATION.CREATE}
        </Styled.ReservationButton>
      </Styled.ButtonWrapper>
    </Styled.ReservationForm>
  );
};

export default ReservationForm;
