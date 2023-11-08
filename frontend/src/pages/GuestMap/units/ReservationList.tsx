import dayjs from 'dayjs';
import { Dispatch, SetStateAction, useContext, useState } from 'react';
import { ReactComponent as CalendarIcon } from 'assets/svg/calendar.svg';
import { ReactComponent as DeleteIcon } from 'assets/svg/delete.svg';
import { ReactComponent as EditIcon } from 'assets/svg/edit.svg';
import ColorDot from 'components/ColorDot/ColorDot';
import IconButton from 'components/IconButton/IconButton';
import Input from 'components/Input/Input';
import ManagerReservationListItem from 'components/ManagerReservationListItem/ManagerReservationListItem';
import Select from 'components/Select/Select';
import DATE from 'constants/date';
import useGuestReservationsV2 from 'hooks/query-v2/useGuestReservationsV2';
import useGuestReservations from 'hooks/query/useGuestReservations';
import useGuestSpace from 'hooks/query/useGuestSpace';
import { AccessTokenContext } from 'providers/AccessTokenProvider';
import { MapItem, Reservation } from 'types/common';
import { formatDate, isPastTime } from 'utils/datetime';
import { getReservationStatus } from 'utils/reservation';
import { isNullish } from 'utils/type';
import { GuestMapFormContext } from '../providers/GuestMapFormProvider';
import * as Styled from './ReservationList.styled';

interface Props {
  map: MapItem;
  // selectedSpaceId: number | null;
  onEdit: (reservation: Reservation) => void;
  onDelete: (reservation: Reservation) => void;
}

const ReservationList = ({
  map: { mapId },
  // selectedSpaceId,
  onDelete,
  onEdit,
}: Props) => {
  const { accessToken } = useContext(AccessTokenContext);
  const { spaceList, selectedSpaceId, setSelectedSpaceId } = useContext(GuestMapFormContext);

  const [date, setDate] = useState<string>(dayjs().format('YYYY-MM-DD'));

  const {
    data: reservations,
    isLoadingError,
    isSuccess,
  } = useGuestReservationsV2(
    {
      mapId: mapId,
      spaceId: +selectedSpaceId,
      date: formatDate(dayjs(date)),
    },
    {
      enabled: !!selectedSpaceId && !isNullish(selectedSpaceId) && dayjs(date).isValid(),
    }
  );

  const { data: space } = useGuestSpace(
    {
      mapId,
      spaceId: +selectedSpaceId,
    },
    {
      enabled: !!selectedSpaceId && !isNullish(selectedSpaceId),
    }
  );

  const handleDate = (e: React.ChangeEvent<HTMLInputElement>) => {
    setDate(e.target.value);
  };

  const getSpaceOptions = () => {
    return (
      spaceList?.map((space) => ({
        value: `${space.id}`,
        children: (
          <Styled.SpaceOption>
            <ColorDot size="medium" color={space.color} />
            {space.name}
          </Styled.SpaceOption>
        ),
      })) ?? []
    );
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
      <Styled.InputWrapper>
        <Select
          name="space"
          label="공간 선택"
          options={getSpaceOptions()}
          value={String(selectedSpaceId)}
          onChange={(id) => setSelectedSpaceId?.(id)}
        />
      </Styled.InputWrapper>
      <>
        {isLoadingError && (
          <Styled.Message>
            예약 목록을 불러오는 데 문제가 생겼어요!
            <br />
            새로 고침으로 다시 시도해주세요.
          </Styled.Message>
        )}

        {isSuccess && reservations?.data.reservations?.length === 0 && (
          <Styled.MessageWrapper>
            <Styled.Message>예약이 없습니다.</Styled.Message>
          </Styled.MessageWrapper>
        )}

        {isSuccess && (reservations?.data.reservations?.length ?? 0) > 0 && (
          <Styled.ReservationList role="list">
            {reservations?.data.reservations?.map((reservation) => {
              const isControlAvailable = (() => {
                if (isPastTime(new Date(reservation.endDateTime))) {
                  return false;
                }
                // TODO: 속성이 없음!
                // if (!accessToken && reservation.isLoginReservation) {
                //   return false;
                // }

                // if (accessToken && !reservation.isMyReservation) {
                //   return false;
                // }

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
                          onClick={() => onEdit(reservation)}
                          aria-label="수정"
                        >
                          <EditIcon width="100%" height="100%" />
                        </IconButton>
                        <IconButton
                          size="small"
                          onClick={() => onDelete(reservation)}
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
