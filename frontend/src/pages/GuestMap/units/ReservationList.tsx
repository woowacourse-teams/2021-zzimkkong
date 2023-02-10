import dayjs from 'dayjs';
import { useState } from 'react';
import DateInput from 'components/DateInput/DateInput';
import { MapItem } from 'types/common';
import * as Styled from './ReservationList.styled';

interface Props {
  map: MapItem;
}

const ReservationList = ({ map: { mapId } }: Props) => {
  const [date, setDate] = useState(dayjs());

  return (
    <Styled.ReservationListWrapper>
      <DateInput date={date} setDate={setDate} />
    </Styled.ReservationListWrapper>
  );
};

export default ReservationList;
