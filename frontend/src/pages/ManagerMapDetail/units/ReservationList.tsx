import { useMemo, useState } from 'react';
import { ReactComponent as DeleteIcon } from 'assets/svg/delete.svg';
import { ReactComponent as EditIcon } from 'assets/svg/edit.svg';
import Button from 'components/Button/Button';
import IconButton from 'components/IconButton/IconButton';
import ManagerReservationListItem from 'components/ManagerReservationListItem/ManagerReservationListItem';
import Panel from 'components/Panel/Panel';
import PATH, { HREF } from 'constants/path';
import { Order, Reservation, SpaceReservation } from 'types/common';
import { getReservationStatus } from 'utils/reservation';
import { sortReservations } from 'utils/sort';
import * as Styled from './ReservationList.styles';

interface Props {
  reservations: SpaceReservation[];
  selectedMapId: number;
  isLoading: boolean;
  onCreateReservation: (spaceId: number) => void;
  onEditReservation: (reservation: Reservation, spaceId: number) => void;
  onDeleteReservation: (reservationId: number, spaceId: number) => void;
}

const ReservationList = ({
  reservations,
  selectedMapId,
  isLoading,
  onCreateReservation,
  onEditReservation,
  onDeleteReservation,
}: Props): JSX.Element => {
  const [spacesOrder, setSpacesOrder] = useState<Order>(Order.Ascending);

  const sortedReservations = useMemo(
    () => sortReservations(reservations, spacesOrder),
    [reservations, spacesOrder]
  );

  const handleClickSpacesOrder = () => {
    setSpacesOrder((prev) => (prev === Order.Ascending ? Order.Descending : Order.Ascending));
  };

  return (
    <>
      {isLoading && (
        <Styled.NoticeWrapper>
          <Styled.NoticeMessage>공간을 로딩 중입니다</Styled.NoticeMessage>
        </Styled.NoticeWrapper>
      )}

      {!isLoading &&
        !reservations.length &&
        (selectedMapId === null ? (
          <Styled.NoticeWrapper>
            <Styled.NoticeMessage>생성한 맵이 없습니다.</Styled.NoticeMessage>
            <Styled.NoticeLink to={PATH.MANAGER_MAP_CREATE}>맵 생성하러 가기</Styled.NoticeLink>
          </Styled.NoticeWrapper>
        ) : (
          <Styled.NoticeWrapper>
            <Styled.NoticeMessage>생성한 공간이 없습니다.</Styled.NoticeMessage>
            <Styled.NoticeLink to={HREF.MANAGER_SPACE_EDIT(selectedMapId)}>
              공간 생성하러 가기
            </Styled.NoticeLink>
          </Styled.NoticeWrapper>
        ))}

      <Styled.ReservationsContainer>
        {selectedMapId && reservations.length > 0 && (
          <Styled.SpacesOrderButton variant="text" onClick={handleClickSpacesOrder}>
            {spacesOrder === 'ascending' ? '오름차순 △' : '내림차순 ▽'}
          </Styled.SpacesOrderButton>
        )}
        <Styled.SpaceList>
          {sortedReservations &&
            sortedReservations.map(({ spaceId, spaceName, spaceColor, reservations }) => (
              <Panel key={`space-${spaceId}`} role="listitem">
                <Panel.Header dotColor={spaceColor}>
                  <Styled.PanelHeadWrapper>
                    <Panel.Title>{spaceName}</Panel.Title>
                    <Button
                      variant="primary-text"
                      size="dense"
                      onClick={() => onCreateReservation(spaceId)}
                    >
                      예약 추가하기
                    </Button>
                  </Styled.PanelHeadWrapper>
                </Panel.Header>
                <Panel.Content>
                  {reservations.length === 0 ? (
                    <Styled.PanelMessage>등록된 예약이 없습니다</Styled.PanelMessage>
                  ) : (
                    <>
                      {reservations.map((reservation) => (
                        <ManagerReservationListItem
                          key={`reservation-${reservation.id}`}
                          reservation={reservation}
                          status={getReservationStatus(
                            reservation.startDateTime,
                            reservation.endDateTime
                          )}
                          control={
                            <Styled.IconButtonWrapper>
                              <IconButton
                                size="small"
                                onClick={() => onEditReservation(reservation, spaceId)}
                              >
                                <EditIcon width="100%" height="100%" />
                              </IconButton>

                              <IconButton
                                size="small"
                                onClick={() => onDeleteReservation(reservation.id, spaceId)}
                              >
                                <DeleteIcon width="100%" height="100%" />
                              </IconButton>
                            </Styled.IconButtonWrapper>
                          }
                        />
                      ))}
                    </>
                  )}
                </Panel.Content>
              </Panel>
            ))}
        </Styled.SpaceList>
      </Styled.ReservationsContainer>
    </>
  );
};

export default ReservationList;
