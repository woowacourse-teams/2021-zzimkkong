import Header from 'components/Header/Header';
import Input from 'components/Input/Input';
import Layout from 'components/Layout/Layout';
import * as Styled from './UserReservation.styles';
import { ReactComponent as CalendarIcon } from 'assets/svg/calendar.svg';
import ReservationListItem from 'components/ReservationListItem/ReservationListItem';
import Button from 'components/Button/Button';
import useInput from 'hooks/useInput';
import { formatDate, formatTime } from 'utils/datetime';

const UserReservation = () => {
  const [name, onChangeName] = useInput('');
  const [description, onChangeDescription] = useInput('');
  const [date, onChangeDate] = useInput(formatDate(new Date()));
  const [startTime, onChangeStartTime] = useInput(formatTime(new Date()));
  const [endTime, onChangeEndTime] = useInput(formatTime(new Date()));

  return (
    <>
      <Header />
      <Layout>
        <Styled.ReservationForm>
          <Styled.Section>
            <Styled.PageHeader>회의실 1</Styled.PageHeader>
            <Input label="이름" value={name} onChange={onChangeName} autoFocus required />
            <Input label="사용 목적" value={description} onChange={onChangeDescription} required />
            <Input
              type="date"
              label="날짜"
              icon={<CalendarIcon />}
              value={date}
              onChange={onChangeDate}
              required
            />
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
                onChange={onChangeEndTime}
                required
              />
            </Styled.InputWrapper>
          </Styled.Section>
          <Styled.Section>
            <Styled.PageHeader>기존 예약 목록</Styled.PageHeader>
            <Styled.ReservationList role="list">
              <ReservationListItem
                reservation={{
                  name: '체프',
                  description: '맛있는 커피를 내리는 법',
                  startDateTime: new Date('2021-07-12T18:00:00'),
                  endDateTime: new Date('2021-07-12T19:00:00'),
                }}
              />
              <ReservationListItem
                reservation={{
                  name: '썬',
                  description: '도예 원데이 클래스',
                  startDateTime: new Date('2021-07-12T19:00:00'),
                  endDateTime: new Date('2021-07-12T20:00:00'),
                }}
              />
              <ReservationListItem
                reservation={{
                  name: '체프',
                  description: '맛있는 커피를 내리는 법',
                  startDateTime: new Date('2021-07-12T18:00:00'),
                  endDateTime: new Date('2021-07-12T19:00:00'),
                }}
              />
            </Styled.ReservationList>
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
