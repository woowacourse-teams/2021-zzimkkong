import { AxiosError } from 'axios';
import dayjs from 'dayjs';
import { useState } from 'react';
import { ReactComponent as DeleteIcon } from 'assets/svg/delete.svg';
import { ReactComponent as EditIcon } from 'assets/svg/edit.svg';
import DateInput from 'components/DateInput/DateInput';
import IconButton from 'components/IconButton/IconButton';
import ManagerReservationListItem from 'components/ManagerReservationListItem/ManagerReservationListItem';
import Panel from 'components/Panel/Panel';
import MESSAGE from 'constants/message';
import useManagerMapReservations from 'hooks/query/useManagerMapReservations';
import { MapItem } from 'types/common';
import { ErrorResponse } from 'types/response';
import { formatDate } from 'utils/datetime';
import { getReservationStatus } from 'utils/reservation';
import { isNullish } from 'utils/type';
import * as Styled from './ReservationList.styled';

interface Props {
  map: MapItem;
}

const ReservationList = ({ map: { mapId } }: Props) => {
  const [date, setDate] = useState(dayjs());

  //   TODO 이제 매니저만 사용하지 않음. 훅 네이밍 변경 필요.
  const { data: mapReservations } = useManagerMapReservations(
    {
      mapId: mapId,
      date: formatDate(date),
    },
    {
      enabled: !isNullish(mapId),
      onError: (error: AxiosError<ErrorResponse>) => {
        alert(error.response?.data?.message ?? MESSAGE.MANAGER_MAIN.UNEXPECTED_GET_DATA_ERROR);
      },
    }
  );

  return (
    <Styled.ReservationListWrapper>
      <DateInput date={date} setDate={setDate} />
      {mapReservations &&
        mapReservations.data.data?.map(({ spaceId, spaceName, spaceColor, reservations }) => (
          <Panel key={`space-${spaceId}`} role="listitem">
            <Panel.Header dotColor={spaceColor}>
              <Styled.PanelHeadWrapper>
                <Panel.Title>{spaceName}</Panel.Title>
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
                            // onClick={() => onEditReservation(reservation, spaceId)}
                          >
                            <EditIcon width="100%" height="100%" />
                          </IconButton>

                          <IconButton
                            size="small"
                            // onClick={() => onDeleteReservation(reservation.id, spaceId)}
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
      {/* {
            mapReservations.data.data.map(({ reservations }) => (

        } */}

      {/* {reservations?.data.data.map(({reservations}) => (


        <ReservationItem
          key={`reservation-${reservation.id}`}
          reservation={reservation}
          status={getReservationStatus(reservation.startDateTime, reservation.endDateTime)}
          control={
            <Styled.IconButtonWrapper>
              <IconButton size="small" onClick={() => onEditReservation(reservation, spaceId)}>
                <EditIcon width="100%" height="100%" />
              </IconButton>

              <IconButton size="small" onClick={() => onDeleteReservation(reservation.id, spaceId)}>
                <DeleteIcon width="100%" height="100%" />
              </IconButton>
            </Styled.IconButtonWrapper>
          }
        />
      ))} */}
    </Styled.ReservationListWrapper>
  );
};

export default ReservationList;
