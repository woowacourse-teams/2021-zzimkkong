import { AxiosError } from 'axios';
import React, { useEffect, useState } from 'react';
import { useMutation } from 'react-query';
import { useHistory } from 'react-router-dom';
import { deleteGuestReservation, queryGuestNonLoginReservations } from 'api/guestReservation';
import GrayLogoImage from 'assets/images/gray-logo.png';
import { ReactComponent as DeleteIcon } from 'assets/svg/delete.svg';
import { ReactComponent as EditIcon } from 'assets/svg/edit.svg';
import Button from 'components/Button/Button';
import IconButton from 'components/IconButton/IconButton';
import Loader from 'components/Loader/Loader';
import MemberReservationListItem from 'components/MemberReservationListItem/MemberReservationListItem';
import MESSAGE from 'constants/message';
import { HREF } from 'constants/path';
import { ManagerSpaceAPI, MemberReservation } from 'types/common';
import { ErrorResponse } from 'types/response';
import { formatDate } from 'utils/datetime';
import api from '../../../api/api';
import useNonLoginReservations from '../../../hooks/infiniteQuery/useNonLoginReservations';
import PasswordInputModal from '../../GuestMap/units/PasswordInputModal';
import * as Styled from './GuestNonLoginReservationSearchResult.styles';

interface Props {
  userName: string;
  searchStartTime: string;
}

const GuestNonLoginReservationSearchResult = ({
  userName,
  searchStartTime,
}: Props): JSX.Element => {
  const history = useHistory();
  const [passwordInputModalOpen, setPasswordInputModalOpen] = useState(false);
  const [selectedReservation, setSelectedReservation] = useState<MemberReservation>();

  const {
    refetch,
    isFetching: isFetchingReservations,
    fetchNextPage: fetchNextReservations,
    hasNextPage: hasNextReservations,
    flattedResults: flattedReservations,
  } = useNonLoginReservations(userName, searchStartTime);

  const removeReservation = useMutation(deleteGuestReservation, {
    onSuccess: () => {
      refetch();
      window.alert(MESSAGE.RESERVATION.DELETE_SUCCESS);
      setPasswordInputModalOpen(false);
    },

    onError: (error: AxiosError<ErrorResponse>) => {
      alert(error.response?.data.message ?? MESSAGE.RESERVATION.UNEXPECTED_DELETE_ERROR);
    },
  });

  const handleDelete = (reservation: MemberReservation) => {
    setPasswordInputModalOpen(true);
    setSelectedReservation(reservation);
  };

  const handleDeleteReservation = (passwordInput: string) => {
    if (passwordInput === null || selectedReservation === null) return;

    removeReservation.mutate({
      mapId: Number(selectedReservation?.mapId),
      spaceId: Number(selectedReservation?.spaceId),
      password: passwordInput,
      reservationId: Number(selectedReservation?.id),
    });
  };

  const handleEdit = (reservation: MemberReservation) => {
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

  useEffect(() => {
    refetch();
  }, [refetch, searchStartTime]);

  return (
    <>
      <Styled.HorizontalLine />
      <Styled.FlexCenter>
        {!isFetchingReservations && flattedReservations.length === 0 && (
          <Styled.StatusContainer>
            <Styled.Image src={GrayLogoImage} alt="Not Found" />
            <Styled.PageHeader>검색 결과가 없습니다.</Styled.PageHeader>
          </Styled.StatusContainer>
        )}
        {isFetchingReservations && (
          <Styled.StatusContainer>
            <Loader />
          </Styled.StatusContainer>
        )}
      </Styled.FlexCenter>
      <Styled.List role="list">
        {flattedReservations.map((reservation) => (
          <MemberReservationListItem
            key={reservation.id}
            reservation={reservation}
            control={
              <>
                <IconButton size="small" onClick={() => handleEdit(reservation)} aria-label="수정">
                  <EditIcon width="100%" height="100%" />
                </IconButton>
                <IconButton
                  size="small"
                  onClick={() => handleDelete(reservation)}
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
      <PasswordInputModal
        open={passwordInputModalOpen}
        onClose={() => setPasswordInputModalOpen(false)}
        onSubmit={handleDeleteReservation}
      />
    </>
  );
};

export default GuestNonLoginReservationSearchResult;
