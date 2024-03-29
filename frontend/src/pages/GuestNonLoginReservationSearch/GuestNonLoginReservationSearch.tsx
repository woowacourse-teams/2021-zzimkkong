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
  const [searchStartTime, setSearchStartTime] = useState<string | null>(null);

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
            message="지난 예약은 조회되지 않습니다."
            autoFocus
            required
          />
          <Button variant="primary" size="large" fullWidth>
            조회
          </Button>
        </Styled.Form>
        {searchStartTime !== null && (
          <GuestNonLoginReservationSearchResult
            userName={userName}
            searchStartTime={searchStartTime}
          />
        )}
      </Styled.Container>
    </>
  );
};

export default GuestNonLoginReservationSearch;
