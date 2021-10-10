import { ReactComponent as DeleteIcon } from 'assets/svg/delete.svg';
import { ReactComponent as EditIcon } from 'assets/svg/edit.svg';
import Drawer from 'components/Drawer/Drawer';
import IconButton from 'components/IconButton/IconButton';
import ReservationListItem from 'components/ReservationListItem/ReservationListItem';
import useGuestReservations from 'hooks/query/useGuestReservations';
import { Reservation, Space } from 'types/common';
import { formatDate, isPastDay, isPastTime } from 'utils/datetime';
import * as Styled from './ReservationDrawer.styles';

interface Props {
  mapId: number;
  space: Space;
  date: Date;
  open: boolean;
  onClose: () => void;
  onClickReservation: () => void;
  onEdit: (reservation: Reservation) => void;
  onDelete: (reservation: Reservation) => void;
}

const ReservationDrawer = ({
  mapId,
  space,
  date,
  open,
  onClose,
  onClickReservation,
  onEdit,
  onDelete,
}: Props): JSX.Element => {
  const getReservations = useGuestReservations({
    mapId,
    spaceId: space.id,
    date: formatDate(date),
  });

  const reservations = getReservations.data?.data?.reservations ?? [];
  const isPastDate = isPastDay(date);

  return (
    <Drawer open={open} placement="bottom" onClose={onClose}>
      <Styled.SpaceTitle>
        <Styled.ColorDot color={space.color} />
        {space.name}
      </Styled.SpaceTitle>
      <Styled.ReservationContainer>
        {getReservations.isLoadingError && (
          <Styled.Message>
            예약 목록을 불러오는 데 문제가 생겼어요!
            <br />
            새로 고침으로 다시 시도해주세요.
          </Styled.Message>
        )}
        {getReservations.isSuccess && reservations?.length === 0 && !isPastDate && (
          <Styled.Message>오늘의 첫 예약을 잡아보세요!</Styled.Message>
        )}
        {getReservations.isSuccess && reservations?.length === 0 && isPastDate && (
          <Styled.Message>과거 날짜엔 예약할 수 없습니다!</Styled.Message>
        )}
        {getReservations.isSuccess && reservations.length > 0 && (
          <Styled.ReservationList role="list">
            {reservations.map((reservation: Reservation) => (
              <ReservationListItem
                key={reservation.id}
                data-testid={`reservation-${reservation.id}`}
                reservation={reservation}
                control={
                  !isPastTime(new Date(reservation.endDateTime)) && (
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
            ))}
          </Styled.ReservationList>
        )}
      </Styled.ReservationContainer>

      {!isPastDate && (
        <Styled.ReservationButton
          variant="primary"
          size="large"
          fullWidth
          onClick={onClickReservation}
        >
          예약하기
        </Styled.ReservationButton>
      )}
    </Drawer>
  );
};

export default ReservationDrawer;
