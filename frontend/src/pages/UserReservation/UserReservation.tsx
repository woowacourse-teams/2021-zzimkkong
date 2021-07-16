import { AxiosError } from 'axios';
import { FormEventHandler } from 'react';
import { useMutation } from 'react-query';
import { useHistory } from 'react-router-dom';
import { postReservation } from 'api/reservation';
import { ReactComponent as CalendarIcon } from 'assets/svg/calendar.svg';
import Button from 'components/Button/Button';
import Header from 'components/Header/Header';
import Input from 'components/Input/Input';
import Layout from 'components/Layout/Layout';
import ReservationListItem from 'components/ReservationListItem/ReservationListItem';
import MESSAGE from 'constants/message';
import REGEXP from 'constants/regexp';
import useInput from 'hooks/useInput';
import useReservations from 'hooks/useReservations';
import { formatDate, formatTime } from 'utils/datetime';
import * as Styled from './UserReservation.styles';

const UserReservation = (): JSX.Element => {
  // Note: 1번 맵의 1번 공간에 대해서만 예약되도록 임시 구현
  const mapId = 1;
  const spaceId = 1;
  const spaceName = '회의실 1';
  const now = new Date();

  const history = useHistory();

  const [name, onChangeName] = useInput('');
  const [description, onChangeDescription] = useInput('');
  const [date, onChangeDate] = useInput(formatDate(now));
  const [startTime, onChangeStartTime] = useInput(formatTime(now));
  const [endTime, onChangeEndTime] = useInput(formatTime(new Date(now.getTime() + 1000 * 60 * 60)));
  const [password, onChangePassword] = useInput('');

  const getReservations = useReservations({ mapId, spaceId, date });

  const reservations = getReservations.data?.data?.reservations ?? [];

  const createReservation = useMutation(postReservation, {
    onSuccess: () => {
      history.push('/');
    },
    onError: (error: AxiosError<Error>) => {
      alert(error.response?.data.message ?? MESSAGE.RESERVATION.UNEXPECTED_ERROR);
    },
  });

  const handleSubmit: FormEventHandler<HTMLFormElement> = (event) => {
    event.preventDefault();

    if (createReservation.isLoading) return;

    const startDateTime = new Date(`${date}T${startTime}Z`);
    const endDateTime = new Date(`${date}T${endTime}Z`);

    const mapId = 1;
    const reservation = {
      spaceId: 1,
      name,
      description,
      password,
      startDateTime,
      endDateTime,
    };

    createReservation.mutate({ reservation, mapId });
  };

  return (
    <>
      <Header />
      <Layout>
        <Styled.ReservationForm onSubmit={handleSubmit}>
          <Styled.Section>
            <Styled.PageHeader>{spaceName}</Styled.PageHeader>
            <Styled.InputWrapper>
              <Input label="이름" value={name} onChange={onChangeName} autoFocus required />
            </Styled.InputWrapper>
            <Styled.InputWrapper>
              <Input
                label="사용 목적"
                value={description}
                onChange={onChangeDescription}
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
                pattern={REGEXP.RESERVATION_PASSWORD.source}
                message="숫자 4자리를 입력해주세요."
                required
              />
            </Styled.InputWrapper>
          </Styled.Section>
          <Styled.Section>
            <Styled.PageHeader>{date}의 예약 목록</Styled.PageHeader>
            {getReservations.isLoading && <Styled.Message>불러오는 중입니다...</Styled.Message>}
            {getReservations.isFetched && reservations.length === 0 && (
              <Styled.Message>오늘의 첫 예약을 잡아보세요!</Styled.Message>
            )}
            {getReservations.isFetched && reservations.length > 0 && (
              <Styled.ReservationList role="list">
                {reservations?.map((reservation) => (
                  <ReservationListItem key={reservation.id} reservation={reservation} />
                ))}
              </Styled.ReservationList>
            )}
          </Styled.Section>

          <Styled.ButtonWrapper>
            <Button fullWidth variant="primary" size="large">
              예약하기
            </Button>
          </Styled.ButtonWrapper>
        </Styled.ReservationForm>
      </Layout>
    </>
  );
};

export default UserReservation;
