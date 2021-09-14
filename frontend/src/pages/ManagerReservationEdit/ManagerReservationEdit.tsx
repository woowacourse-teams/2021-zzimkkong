import { AxiosError } from 'axios';
import { FormEventHandler } from 'react';
import { useMutation } from 'react-query';
import { useHistory, useLocation } from 'react-router-dom';
import { putManagerReservation } from 'api/managerReservation';
import { ReactComponent as CalendarIcon } from 'assets/svg/calendar.svg';
import Button from 'components/Button/Button';
import Header from 'components/Header/Header';
import Input from 'components/Input/Input';
import Layout from 'components/Layout/Layout';
import PageHeader from 'components/PageHeader/PageHeader';
import ReservationListItem from 'components/ReservationListItem/ReservationListItem';
import MESSAGE from 'constants/message';
import PATH from 'constants/path';
import RESERVATION from 'constants/reservation';
import useGuestReservations from 'hooks/query/useGuestReservations';
import useManagerSpace from 'hooks/query/useManagerSpace';
import useInput from 'hooks/useInput';
import useListenManagerMainState from 'hooks/useListenManagerMainState';
import { GuestMapState } from 'pages/GuestMap/GuestMap';
import { Reservation } from 'types/common';
import { ErrorResponse } from 'types/response';
import { formatDate, formatTime } from 'utils/datetime';
import * as Styled from './ManagerReservationEdit.styles';

interface ManagerReservationEditState {
  mapId: number;
  spaceId: number;
  reservation: Reservation;
  selectedDate: string;
}

const ManagerReservationEdit = (): JSX.Element => {
  const location = useLocation<ManagerReservationEditState>();
  const history = useHistory<GuestMapState>();

  const { mapId, spaceId, reservation, selectedDate } = location.state;

  if (!mapId || !spaceId || !reservation) history.replace(PATH.MANAGER_MAIN);

  useListenManagerMainState({ mapId: Number(mapId) });

  const getSpace = useManagerSpace({ mapId, spaceId });
  const space = getSpace.data?.data.data;

  const availableStartTime = space?.settings?.availableStartTime ?? '';
  const availableEndTime = space?.settings?.availableEndTime ?? '';
  const reservationTimeUnit = space?.settings?.reservationTimeUnit ?? 0;
  const reservationMaximumTimeUnit = space?.settings?.reservationMaximumTimeUnit ?? 0;

  const now = new Date();
  const todayDate = formatDate(new Date());

  const [name, onChangeName] = useInput(reservation.name);
  const [description, onChangeDescription] = useInput(reservation.description);
  const [date, onChangeDate] = useInput(selectedDate);
  const [startTime, onChangeStartTime] = useInput(formatTime(new Date(reservation.startDateTime)));
  const [endTime, onChangeEndTime] = useInput(formatTime(new Date(reservation.endDateTime)));

  const startDateTime = new Date(`${date}T${startTime}Z`);
  const endDateTime = new Date(`${date}T${endTime}Z`);

  const availableStartTimeText = formatTime(new Date(`${todayDate}T${availableStartTime}`));
  const availableEndTimeText = formatTime(new Date(`${todayDate}T${availableEndTime}`));

  const getReservations = useGuestReservations({ mapId, spaceId, date });
  const reservations = getReservations.data?.data?.reservations ?? [];

  const editReservation = useMutation(putManagerReservation, {
    onSuccess: () => {
      history.push(PATH.MANAGER_MAIN, {
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
        <Styled.ReservationForm onSubmit={handleSubmit}>
          <Styled.Section>
            {space && (
              <Styled.PageHeader>
                <Styled.ColorDot color={space.color} size="medium" />
                {space.name}
              </Styled.PageHeader>
            )}
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
                // TODO 현재 적용 X
                // step={60 * reservationTimeUnit}
                min={availableStartTime}
                max={availableEndTime}
                value={startTime}
                onChange={onChangeStartTime}
                required
              />
              <Input
                type="time"
                label="종료 시간"
                // TODO 현재 적용 X
                // step={60 * reservationTimeUnit}
                min={startTime}
                max={availableEndTime}
                value={endTime}
                onChange={onChangeEndTime}
                required
              />

              {/* TODO 현재 NaN으로 표시  */}
              {/* <Styled.TimeFormMessage>
                예약 가능 시간 : {availableStartTimeText} ~ {availableEndTimeText} (최대{' '}
                {formatTimePrettier(reservationMaximumTimeUnit)})
              </Styled.TimeFormMessage> */}
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

export default ManagerReservationEdit;
