import React, { FormEventHandler, useEffect, useState } from 'react';
import Button from 'components/Button/Button';
import Input from 'components/Input/Input';
import Header from '../../components/Header/Header';
import DATE from '../../constants/date';
import useInputs from '../../hooks/useInputs';
import { formatDate, formatTimeWithSecond } from '../../utils/datetime';
import * as Styled from './GuestNonLoginReservationSearch.styles';
import GuestNonLoginReservationSearchResult from './units/GuestNonLoginReservationSearchResult';

interface Form {
  userName: string;
}

const GuestNonLoginReservationSearch = (): JSX.Element => {
  const [{ userName }, onChangeForm] = useInputs<Form>({
    userName: '',
  });
  const [searchStartTime, setSearchStartTime] = useState(() => {
    const nowDateTime = new Date();
    return `${formatDate(nowDateTime)}T${formatTimeWithSecond(nowDateTime)}${
      DATE.TIMEZONE_OFFSET_QUERY_STRING
    }`;
  });

  const handleSubmit: FormEventHandler<HTMLFormElement> = (event) => {
    event.preventDefault();

    const nowDateTime = new Date();
    setSearchStartTime(
      `${formatDate(nowDateTime)}T${formatTimeWithSecond(nowDateTime)}${
        DATE.TIMEZONE_OFFSET_QUERY_STRING
      }`
    );
  };

  return (
    <>
      <Header />
      <Styled.Container>
        <Styled.ListTitle>비회원 예약 조회</Styled.ListTitle>
        <Styled.Form onSubmit={handleSubmit}>
          <Input
            type="userName"
            label="예약자 이름"
            name="userName"
            value={userName}
            onChange={onChangeForm}
            autoFocus
            required
          />
          <Button variant="primary" size="large" fullWidth>
            찾기
          </Button>
        </Styled.Form>
        <GuestNonLoginReservationSearchResult
          userName={userName}
          searchStartTime={searchStartTime}
        />
      </Styled.Container>
    </>
  );
};

export default GuestNonLoginReservationSearch;
