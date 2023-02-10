import dayjs from 'dayjs';
import { useState } from 'react';
import DateInput from 'components/DateInput/DateInput';
import { MapItem } from 'types/common';
import * as Styled from './ReservationList.styled';

interface Props {
  map: MapItem;
  selectedSpaceId: number | null;
}

const ReservationList = ({ map: { mapId }, selectedSpaceId }: Props) => {
  const [date, setDate] = useState(dayjs());

  console.log('selectedSpaceId', selectedSpaceId);

  return (
    <Styled.ReservationListWrapper>
      <DateInput date={date} setDate={setDate} />
    </Styled.ReservationListWrapper>
  );
};

export default ReservationList;
