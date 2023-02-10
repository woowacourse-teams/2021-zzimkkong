import dayjs from 'dayjs';
import { useState } from 'react';
import { ReactComponent as CalendarIcon } from 'assets/svg/calendar.svg';
import Input from 'components/Input/Input';
import DATE from 'constants/date';
import useGuestReservations from 'hooks/query/useGuestReservations';
import { MapItem } from 'types/common';
import { formatDate } from 'utils/datetime';
import { isNullish } from 'utils/type';
import * as Styled from './ReservationList.styled';

interface Props {
  map: MapItem;
  selectedSpaceId: number | null;
}

const ReservationList = ({ map: { mapId }, selectedSpaceId }: Props) => {
  const [date, setDate] = useState<string>(dayjs().format('YYYY-MM-DD'));

  const getReservations = useGuestReservations(
    {
      mapId: mapId,
      spaceId: selectedSpaceId as number,
      date: formatDate(dayjs(date)),
    },
    {
      enabled: !isNullish(selectedSpaceId) && dayjs(date).isValid(),
    }
  );

  const handleDate = (e: React.ChangeEvent<HTMLInputElement>) => {
    setDate(e.target.value);
  };

  return (
    <Styled.ReservationListWrapper>
      <Input
        type="date"
        name="date"
        label="날짜"
        icon={<CalendarIcon />}
        value={date}
        min={DATE.MIN_DATE_STRING}
        max={DATE.MAX_DATE_STRING}
        onChange={handleDate}
        required
      />
    </Styled.ReservationListWrapper>
  );
};

export default ReservationList;
