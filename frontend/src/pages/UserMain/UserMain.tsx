import { AxiosError } from 'axios';
import { useState } from 'react';
import { useMutation } from 'react-query';
import { useHistory, useLocation } from 'react-router-dom';
import { deleteReservation } from 'api/reservation';
import { ReactComponent as Luther } from 'assets/svg/luther.svg';
import { ReactComponent as More } from 'assets/svg/more.svg';
import Button from 'components/Button/Button';
import DateInput from 'components/DateInput/DateInput';
import Header from 'components/Header/Header';
import Layout from 'components/Layout/Layout';
import Modal from 'components/Modal/Modal';
import Panel from 'components/Panel/Panel';
import PinRadio from 'components/PinRadio/PinRadio';
import ReservationListItem from 'components/ReservationListItem/ReservationListItem';
import MESSAGE from 'constants/message';
import PATH from 'constants/path';
import useInput from 'hooks/useInput';
import useReservations from 'hooks/useReservations';
import { Reservation, Space } from 'types/common';
import { formatDate } from 'utils/datetime';
import * as Styled from './UserMain.styles';
import spaceList from './spaceList';

export interface UserMainState {
  spaceId?: Space['spaceId'];
  targetDate?: Date;
}

const UserMain = (): JSX.Element => {
  // Note: 루터회관 14층으로 상정하고 구현. 추후 useSpaces로 대체 필요
  const mapId = 1;

  const [modalOpen, setModalOpen] = useState(false);
  const [selectedReservationId, setSelectedReservationId] = useState(0);
  const [selectedReservation, setSelectedReservation] = useState({});

  const history = useHistory();
  const location = useLocation<UserMainState>();
  const spaceId = location.state?.spaceId;
  const targetDate = location.state?.targetDate;
  const now = new Date();
  const todayDate = new Date(now.getFullYear(), now.getMonth(), now.getDate());

  const [date, onChangeDate] = useInput(formatDate(targetDate ?? new Date()));
  const [selectedSpaceId, onChangeSelectedSpaceId] = useInput(`${spaceId ?? spaceList[0].spaceId}`);
  const getReservations = useReservations({
    mapId,
    spaceId: Number(selectedSpaceId),
    date,
  });
  const reservations = getReservations.data?.data?.reservations ?? [];

  const removeReservation = useMutation(deleteReservation, {
    onSuccess: () => {
      window.alert('예약이 삭제 되었습니다.');
      setModalOpen(false);
    },

    onError: (error: AxiosError<Error>) => {
      alert(error.response?.data.message ?? MESSAGE.RESERVATION.UNEXPECTED_ERROR);
    },
  });

  const selectedSpace =
    spaceList.find((space) => space.spaceId === Number(selectedSpaceId)) ?? spaceList[0];

  const handleSelectModal = (reservation: Reservation) => {
    setModalOpen(true);
    setSelectedReservationId(reservation.id);
    setSelectedReservation(reservation);
  };

  const handleDeleteReservation = (): void => {
    if (window.confirm('예약을 삭제하시겠습니까?')) {
      const password = String(window.prompt('비밀번호 4자리를 입력해주세요.'));
      removeReservation.mutate({ mapId, password, reservationId: selectedReservationId });
    }
  };

  return (
    <>
      <Header />
      <Layout>
        <Styled.PageTitle>우아한테크코스 교육장</Styled.PageTitle>
        <DateInput value={date} onChange={onChangeDate} />
        <Styled.MapContainer>
          <Styled.Map>
            {spaceList?.map(({ spaceId, spaceName, coordinate, textPosition }) => (
              <PinRadio
                key={spaceId}
                name={`map-${mapId}`}
                coordinate={coordinate}
                text={spaceName}
                textPosition={textPosition}
                value={spaceId}
                checked={Number(selectedSpaceId) === spaceId}
                onChange={onChangeSelectedSpaceId}
              />
            ))}
            <Luther />
          </Styled.Map>
        </Styled.MapContainer>
        <Styled.PanelContainer>
          <Panel>
            <Panel.Header bgColor={selectedSpace.color}>
              {new Date(date) > todayDate && (
                <Styled.ReservationLink
                  to={{
                    pathname: PATH.RESERVATION,
                    state: {
                      mapId,
                      spaceId: Number(selectedSpaceId),
                      spaceName: selectedSpace.spaceName,
                      selectedDate: date,
                    },
                  }}
                >
                  예약
                </Styled.ReservationLink>
              )}
              <Panel.Title>{selectedSpace.spaceName}</Panel.Title>
            </Panel.Header>
            <Panel.Content>
              <>
                {getReservations.isLoadingError && (
                  <Styled.Message>
                    예약 목록을 불러오는 데 문제가 생겼어요!
                    <br />
                    새로 고침으로 다시 시도해주세요.
                  </Styled.Message>
                )}
                {getReservations.isLoading && !getReservations.isLoadingError && (
                  <Styled.Message>불러오는 중입니다...</Styled.Message>
                )}
                {getReservations.isSuccess && reservations?.length === 0 && (
                  <Styled.Message>오늘의 첫 예약을 잡아보세요!</Styled.Message>
                )}
                {getReservations.isSuccess && reservations.length > 0 && (
                  <Styled.ReservationList role="list">
                    {reservations.map((reservation: Reservation) => (
                      <ReservationListItem
                        key={reservation.id}
                        reservation={reservation}
                        control={
                          <Button
                            variant="text"
                            size="small"
                            onClick={() => handleSelectModal(reservation)}
                          >
                            <More />
                          </Button>
                        }
                      />
                    ))}
                  </Styled.ReservationList>
                )}
              </>
            </Panel.Content>
          </Panel>
        </Styled.PanelContainer>
      </Layout>
      <Modal open={modalOpen} isClosableDimmer={true} setModalOpen={setModalOpen}>
        <Styled.SelectBox>
          <Styled.SelectButton
            onClick={() =>
              history.push({
                pathname: PATH.RESERVATION_EDIT,
                state: {
                  mapId,
                  spaceId: Number(selectedSpaceId),
                  reservation: selectedReservation,
                  reservationId: selectedReservationId,
                  spaceName: selectedSpace.spaceName,
                  selectedDate: date,
                },
              })
            }
          >
            수정하기
          </Styled.SelectButton>
          <Styled.SelectButton onClick={handleDeleteReservation}>삭제하기</Styled.SelectButton>
        </Styled.SelectBox>
      </Modal>
    </>
  );
};

export default UserMain;
