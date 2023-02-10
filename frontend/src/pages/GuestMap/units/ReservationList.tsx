import dayjs from 'dayjs';
import { useContext, useState } from 'react';
import { ReactComponent as CalendarIcon } from 'assets/svg/calendar.svg';
import { ReactComponent as DeleteIcon } from 'assets/svg/delete.svg';
import { ReactComponent as EditIcon } from 'assets/svg/edit.svg';
import IconButton from 'components/IconButton/IconButton';
import Input from 'components/Input/Input';
import ManagerReservationListItem from 'components/ManagerReservationListItem/ManagerReservationListItem';
import DATE from 'constants/date';
import useGuestReservations from 'hooks/query/useGuestReservations';
import useGuestSpace from 'hooks/query/useGuestSpace';
import { AccessTokenContext } from 'providers/AccessTokenProvider';
import { MapItem } from 'types/common';
import { formatDate, isPastDate } from 'utils/datetime';
import { getReservationStatus } from 'utils/reservation';
import { isNullish } from 'utils/type';
import * as Styled from './ReservationList.styled';

interface Props {
  map: MapItem;
  selectedSpaceId: number | null;
}

const ReservationList = ({ map: { mapId }, selectedSpaceId }: Props) => {
  const { accessToken } = useContext(AccessTokenContext);

  const [date, setDate] = useState<string>(dayjs().format('YYYY-MM-DD'));

  const {
    data: reservations,
    isLoadingError,
    isSuccess,
  } = useGuestReservations(
    {
      mapId: mapId,
      spaceId: selectedSpaceId as number,
      date: formatDate(dayjs(date)),
    },
    {
      enabled: !isNullish(selectedSpaceId) && dayjs(date).isValid(),
    }
  );

  const { data: space } = useGuestSpace(
    {
      mapId,
      spaceId: selectedSpaceId as number,
    },
    {
      enabled: !isNullish(selectedSpaceId),
    }
  );

  const handleDate = (e: React.ChangeEvent<HTMLInputElement>) => {
    setDate(e.target.value);
  };

  return (
    <Styled.ReservationListWrapper>
      <Styled.SpaceTitle>
        <Styled.ColorDot color={space?.data.color ?? 'transparent'} />
        {space?.data.name}
      </Styled.SpaceTitle>
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
      <>
        {isLoadingError && (
          <Styled.Message>
            예약 목록을 불러오는 데 문제가 생겼어요!
            <br />
            새로 고침으로 다시 시도해주세요.
          </Styled.Message>
        )}

        {isSuccess && reservations?.data.reservations?.length === 0 && isPastDate(dayjs(date)) && (
          <Styled.Message>예약이 없습니다.</Styled.Message>
        )}

        {isSuccess && (reservations?.data.reservations?.length ?? 0) > 0 && (
          <Styled.ReservationList role="list">
            {reservations?.data.reservations?.map((reservation) => {
              const isControlAvailable = (() => {
                if (!accessToken && reservation.isLoginReservation) {
                  return false;
                }

                if (accessToken && !reservation.isMyReservation) {
                  return false;
                }

                return true;
              })();

              return (
                <ManagerReservationListItem
                  key={reservation.id}
                  data-testid={`reservation-${reservation.id}`}
                  reservation={reservation}
                  status={getReservationStatus(reservation.startDateTime, reservation.endDateTime)}
                  control={
                    isControlAvailable && (
                      <Styled.IconButtonWrapper>
                        <IconButton
                          size="small"
                          // onClick={() => onEdit(reservation)}
                          aria-label="수정"
                        >
                          <EditIcon width="100%" height="100%" />
                        </IconButton>
                        <IconButton
                          size="small"
                          // onClick={() => onDelete(reservation)}
                          aria-label="삭제"
                        >
                          <DeleteIcon width="100%" height="100%" />
                        </IconButton>
                      </Styled.IconButtonWrapper>
                    )
                  }
                />
              );
            })}
          </Styled.ReservationList>
        )}
      </>
    </Styled.ReservationListWrapper>
  );
};

export default ReservationList;
