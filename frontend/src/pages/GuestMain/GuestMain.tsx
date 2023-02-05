import { AxiosError } from 'axios';
import React, { useState } from 'react';
import { useMutation } from 'react-query';
import { useHistory } from 'react-router-dom';
import { deleteMemberReservation } from 'api/guestReservation';
import { ReactComponent as DeleteIcon } from 'assets/svg/delete.svg';
import { ReactComponent as EditIcon } from 'assets/svg/edit.svg';
import Button from 'components/Button/Button';
import Header from 'components/Header/Header';
import IconButton from 'components/IconButton/IconButton';
import MemberInfo from 'components/MemberInfo/MemberInfo';
import MemberReservationListItem from 'components/MemberReservationListItem/MemberReservationListItem';
import TabLayout from 'components/TabLayout/TabLayout';
import MESSAGE from 'constants/message';
import { HREF } from 'constants/path';
import { TAB_LABEL, TAB_LIST } from 'constants/tab';
import useMemberReservationHistory from 'hooks/infiniteQuery/useMemberReservationHistory';
import useMemberReservations from 'hooks/infiniteQuery/useMemberReservations';
import { MemberReservarion } from 'types/common';
import { ErrorResponse } from 'types/response';
import { formatDate } from 'utils/datetime';
import * as Styled from './GuestMain.styled';

const GuestMain = (): JSX.Element => {
  const history = useHistory();
  const [isHistoryOpened, setIsHistoryOpened] = useState(false);

  const {
    refetch,
    fetchNextPage: fetchNextReservations,
    hasNextPage: hasNextReservations,
    flattedResults: flattedReservations,
  } = useMemberReservations();
  const {
    fetchNextPage: fetchNextReservationHistory,
    hasNextPage: hasNextReservationHistory,
    flattedResults: flattedReservationHistory,
  } = useMemberReservationHistory();

  const removeReservation = useMutation(deleteMemberReservation, {
    onSuccess: () => {
      refetch();
      window.alert(MESSAGE.RESERVATION.DELETE_SUCCESS);
    },
    onError: (error: AxiosError<ErrorResponse>) => {
      alert(error.response?.data.message ?? MESSAGE.RESERVATION.UNEXPECTED_DELETE_ERROR);
    },
  });

  const handleDeleteReservation = (reservation: MemberReservarion) => {
    removeReservation.mutate({
      mapId: reservation.mapId,
      spaceId: reservation.spaceId,
      reservationId: reservation.id,
    });
  };

  const handleEdit = (reservation: MemberReservarion) => {
    history.push({
      pathname: HREF.GUEST_RESERVATION_EDIT(reservation.sharingMapId),
      state: {
        mapId: reservation.mapId,
        spaceId: reservation.spaceId,
        reservation,
        selectedDate: formatDate(new Date(reservation.startDateTime)),
      },
    });
  };

  return (
    <>
      <Header />
      <TabLayout tabList={TAB_LIST} defaultTabLabel={TAB_LABEL.GUEST}>
        <MemberInfo />

        <Styled.Container>
          <Styled.ListTitle>찜꽁한 예약</Styled.ListTitle>

          <Styled.List role="list">
            {flattedReservations.map((reservation) => (
              <MemberReservationListItem
                reservation={reservation}
                onClick={() => history.push(HREF.GUEST_MAP(reservation.sharingMapId))}
                control={
                  <>
                    <IconButton
                      size="small"
                      onClick={() => handleEdit(reservation)}
                      aria-label="수정"
                    >
                      <EditIcon width="100%" height="100%" />
                    </IconButton>
                    <IconButton
                      size="small"
                      onClick={() => handleDeleteReservation(reservation)}
                      aria-label="삭제"
                    >
                      <DeleteIcon width="100%" height="100%" />
                    </IconButton>
                  </>
                }
              />
            ))}
          </Styled.List>
          {hasNextReservations && (
            <Styled.FlexLeft>
              <Button variant="text" onClick={() => fetchNextReservations()}>
                {'더 보기 >'}
              </Button>
            </Styled.FlexLeft>
          )}

          {flattedReservationHistory.length === 0 ? null : isHistoryOpened ? (
            <Styled.HistoryListContainer>
              <Styled.ListTitle>지난 예약</Styled.ListTitle>

              <Styled.List role="list">
                {flattedReservationHistory.map((reservation) => (
                  <MemberReservationListItem
                    reservation={reservation}
                    onClick={() => history.push(HREF.GUEST_MAP(reservation.sharingMapId))}
                  />
                ))}
              </Styled.List>

              {hasNextReservationHistory && (
                <Styled.FlexLeft>
                  <Button onClick={() => fetchNextReservationHistory}>{'더 보기 >'}</Button>
                </Styled.FlexLeft>
              )}
            </Styled.HistoryListContainer>
          ) : (
            <Styled.ButtonContainer>
              <Styled.RoundedButton
                variant="default"
                shape="round"
                onClick={() => setIsHistoryOpened(true)}
              >
                지난 예약 보기
              </Styled.RoundedButton>
            </Styled.ButtonContainer>
          )}
        </Styled.Container>
      </TabLayout>
    </>
  );
};

export default GuestMain;
