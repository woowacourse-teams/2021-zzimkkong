import { AxiosError } from 'axios';
import { FormEventHandler, useEffect, useRef, useState } from 'react';
import { useMutation } from 'react-query';
import { useHistory, useLocation } from 'react-router-dom';
import { deleteReservation } from 'api/reservation';
import { ReactComponent as Delete } from 'assets/svg/delete.svg';
import { ReactComponent as Edit } from 'assets/svg/edit.svg';
import { ReactComponent as Luther } from 'assets/svg/luther.svg';
import { ReactComponent as More } from 'assets/svg/more.svg';
import Button from 'components/Button/Button';
import DateInput from 'components/DateInput/DateInput';
import Header from 'components/Header/Header';
import Input from 'components/Input/Input';
import Layout from 'components/Layout/Layout';
import Modal from 'components/Modal/Modal';
import Panel from 'components/Panel/Panel';
import PinRadio from 'components/PinRadio/PinRadio';
import ReservationListItem from 'components/ReservationListItem/ReservationListItem';
import MESSAGE from 'constants/message';
import PATH from 'constants/path';
import useInput from 'hooks/useInput';
import useReservations from 'hooks/useReservations';
import { Reservation, ScrollPosition, Space } from 'types/common';
import { ErrorResponse } from 'types/response';
import { formatDate } from 'utils/datetime';
import * as Styled from './GuestMain.styles';
import spaceList from './spaceList';

export interface GuestMainState {
  spaceId?: Space['id'];
  targetDate?: Date;
  scrollPosition?: ScrollPosition;
}

const GuestMain = (): JSX.Element => {
  // Note: 루터회관 14층으로 상정하고 구현. 추후 useSpaces로 대체 필요
  const mapId = 1;

  const [modalOpen, setModalOpen] = useState(false);
  const [passwordInputModalOpen, setPasswordInputModalOpen] = useState(false);
  const [selectedReservation, setSelectedReservation] = useState<Reservation>();

  const [passwordInput, onChangePasswordInput] = useInput('');

  const mapRef = useRef<HTMLDivElement | null>(null);

  const history = useHistory();
  const location = useLocation<GuestMainState>();
  const spaceId = Number(location.state?.spaceId);
  const targetDate = location.state?.targetDate;
  const scrollPosition = location.state?.scrollPosition;

  const now = new Date();
  const todayDate = new Date(now.getFullYear(), now.getMonth(), now.getDate());

  const [date, setDate] = useState(targetDate ? new Date(targetDate) : new Date());

  const [selectedSpaceId, onChangeSelectedSpaceId, setSelectedSpaceId] = useInput(
    `${spaceId ?? spaceList[0].spaceId}`
  );
  const getReservations = useReservations({
    mapId,
    spaceId: Number(selectedSpaceId),
    date: formatDate(date),
  });
  const reservations = getReservations.data?.data?.reservations ?? [];

  const reservationAvailable = new Date(date) > todayDate;

  const removeReservation = useMutation(deleteReservation, {
    onSuccess: () => {
      window.alert('예약이 삭제 되었습니다.');
      setModalOpen(false);
      setPasswordInputModalOpen(false);
    },

    onError: (error: AxiosError<ErrorResponse>) => {
      alert(error.response?.data.message ?? MESSAGE.RESERVATION.UNEXPECTED_DELETE_ERROR);
    },
  });

  const selectedSpace =
    spaceList.find((space) => space.spaceId === Number(selectedSpaceId)) ?? spaceList[0];

  const handleSelectModal = (reservation: Reservation) => {
    setModalOpen(true);
    setSelectedReservation(reservation);
  };

  const handleSelectEdit = () => {
    history.push({
      pathname: PATH.GUEST_RESERVATION_EDIT,
      state: {
        mapId,
        spaceId: Number(selectedSpaceId),
        reservation: selectedReservation,
        spaceName: selectedSpace.spaceName,
        selectedDate: formatDate(date),
      },
    });
  };

  const handleSelectDelete = (): void => {
    setPasswordInputModalOpen(true);
  };

  const handleDeleteReservation: FormEventHandler<HTMLFormElement> = (event) => {
    event.preventDefault();

    removeReservation.mutate({
      mapId,
      spaceId,
      password: passwordInput,
      reservationId: Number(selectedReservation?.id),
    });
  };

  useEffect(() => {
    setDate(targetDate ? new Date(targetDate) : new Date());
  }, [targetDate]);

  useEffect(() => {
    setSelectedSpaceId(String(spaceId));
  }, [setSelectedSpaceId, spaceId]);

  useEffect(() => {
    if (!scrollPosition) return;

    mapRef?.current?.scrollTo(scrollPosition.x ?? 0, scrollPosition.y ?? 0);
  }, [scrollPosition]);

  return (
    <>
      <Header />
      <Layout>
        <Styled.PageWithBottomButton hasBottomButton={reservationAvailable}>
          <Styled.PageTitle>우아한테크코스 교육장</Styled.PageTitle>
          <DateInput date={date} setDate={setDate} />
          <Styled.MapContainer ref={mapRef}>
            <Styled.MapItem>
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
            </Styled.MapItem>
          </Styled.MapContainer>
          <Styled.PanelContainer>
            <Panel>
              <Panel.Header dotColor={selectedSpace.spaceColor}>
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
        </Styled.PageWithBottomButton>
        {reservationAvailable && (
          <Styled.ReservationLink
            to={{
              pathname: PATH.GUEST_RESERVATION,
              state: {
                mapId,
                spaceId: Number(selectedSpaceId),
                spaceName: selectedSpace.spaceName,
                selectedDate: formatDate(date),
                scrollPosition: { x: mapRef?.current?.scrollLeft, y: mapRef?.current?.scrollTop },
              },
            }}
          >
            예약하기
          </Styled.ReservationLink>
        )}
      </Layout>
      <Modal open={modalOpen} isClosableDimmer={true} onClose={() => setModalOpen(false)}>
        <Styled.SelectBox>
          <Styled.SelectButton onClick={handleSelectEdit}>
            <Edit />
            수정하기
          </Styled.SelectButton>
          <Styled.SelectButton onClick={handleSelectDelete}>
            <Delete />
            삭제하기
          </Styled.SelectButton>
        </Styled.SelectBox>
      </Modal>
      <Modal
        open={passwordInputModalOpen}
        isClosableDimmer={true}
        onClose={() => setModalOpen(false)}
      >
        <Modal.Header>예약시 사용하신 비밀번호를 입력해주세요.</Modal.Header>
        <Modal.Inner>
          <form onSubmit={handleDeleteReservation}>
            <Input
              type="password"
              label="비밀번호"
              minLength={4}
              maxLength={4}
              value={passwordInput}
              onChange={onChangePasswordInput}
            />
            <Styled.DeleteModalContainer>
              <Button variant="text" type="button" onClick={() => setPasswordInputModalOpen(false)}>
                취소
              </Button>
              <Button variant="text" type="submit">
                확인
              </Button>
            </Styled.DeleteModalContainer>
          </form>
        </Modal.Inner>
      </Modal>
    </>
  );
};

export default GuestMain;
