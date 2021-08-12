import { AxiosError } from 'axios';
import { FormEventHandler } from 'react';
import { useMutation } from 'react-query';
import { useHistory, useLocation, useParams } from 'react-router-dom';
import { putReservation } from 'api/guestReservation';
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
import { MapItem, Reservation, Space } from 'types/common';
import { ErrorResponse } from 'types/response';
import { formatDate, formatTime } from 'utils/datetime';
import * as Styled from './GuestReservationEdit.styles';

interface GuestReservationEditState {
  mapId: number;
  reservation: Reservation;
  spaceId: Space['id'];
  spaceName: Space['name'];
  selectedDate: string;
}

interface URLParameter {
  sharingMapId: MapItem['sharingMapId'];
}

const GuestReservationEdit = (): JSX.Element => {
  const location = useLocation<GuestReservationEditState>();
  const history = useHistory<GuestMapState>();
  const { sharingMapId } = useParams<URLParameter>();

  const { mapId, spaceId, reservation, spaceName, selectedDate } = location.state;

  if (!mapId || !spaceId || !spaceName || !reservation) history.replace(`/guest/${sharingMapId}`);

  const now = new Date();

  const [name, onChangeName] = useInput(reservation.name);
  const [description, onChangeDescription] = useInput(reservation.description);
  const [date, onChangeDate] = useInput(selectedDate);
  const [startTime, onChangeStartTime] = useInput(formatTime(new Date(reservation.startDateTime)));
  const [endTime, onChangeEndTime] = useInput(formatTime(new Date(reservation.endDateTime)));
  const [password, onChangePassword] = useInput();

  const startDateTime = new Date(`${date}T${startTime}Z`);
  const endDateTime = new Date(`${date}T${endTime}Z`);

  const getReservations = useGuestReservations({ mapId, spaceId, date });
  const reservations = getReservations.data?.data?.reservations ?? [];

  const editReservation = useMutation(putReservation, {
    onSuccess: () => {
      history.push(`/guest/${sharingMapId}`, {
        spaceId,
        targetDate: new Date(`${date}T${startTime}`),
      });
    },

    onError: (error: AxiosError<ErrorResponse>) => {
      alert(error.response?.data.message ?? MESSAGE.RESERVATION.UNEXPECTED_ERROR);
    },
  });

  const handleSubmit: FormEventHandler<HTMLFormElement> = (event) => {
    event.preventDefault();

    if (editReservation.isLoading) return;

    const editReservationParams = {
      name,
      description,
      password,
      startDateTime,
      endDateTime,
    };

    editReservation.mutate({
      reservation: editReservationParams,
      mapId,
      spaceId,
      reservationId: reservation.id,
    });
  };

  return (
    <>
      <Header />
      <Layout>
        <PageHeader title={spaceName} />
        <Styled.ReservationForm onSubmit={handleSubmit}>
          <Styled.Section>
            <Styled.InputWrapper>
              <Input
                label="이름"
                value={name}
                onChange={onChangeName}
                maxLength={RESERVATION.NAME.MAX_LENGTH}
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
                value={startTime}
                onChange={onChangeStartTime}
                required
              />
              <Input
                type="time"
                label="종료 시간"
                value={endTime}
                min={startTime}
                onChange={onChangeEndTime}
                required
              />
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
                message="예약하실 때 사용하신 비밀번호 4자리를 입력해주세요."
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
              예약 수정하기
            </Button>
          </Styled.ButtonWrapper>
        </Styled.ReservationForm>
      </Layout>
    </>
  );
};

export default GuestReservationEdit;
