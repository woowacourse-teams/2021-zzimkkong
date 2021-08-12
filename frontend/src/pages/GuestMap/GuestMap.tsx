import { AxiosError } from 'axios';
import { FormEventHandler, useMemo, useState } from 'react';
import { useMutation } from 'react-query';
import { useHistory, useLocation, useParams } from 'react-router-dom';
import { deleteReservation } from 'api/guestReservation';
import { ReactComponent as Delete } from 'assets/svg/delete.svg';
import { ReactComponent as Edit } from 'assets/svg/edit.svg';
import { ReactComponent as More } from 'assets/svg/more.svg';
import Button from 'components/Button/Button';
import DateInput from 'components/DateInput/DateInput';
import Header from 'components/Header/Header';
import Input from 'components/Input/Input';
import Layout from 'components/Layout/Layout';
import Modal from 'components/Modal/Modal';
import Panel from 'components/Panel/Panel';
import ReservationListItem from 'components/ReservationListItem/ReservationListItem';
import MESSAGE from 'constants/message';
import PALETTE from 'constants/palette';
import useGuestMap from 'hooks/useGuestMap';
import useGuestReservations from 'hooks/useGuestReservations';
import useInput from 'hooks/useInput';
import useSpaces from 'hooks/useSpaces';
import { Area, MapDrawing, MapItem, Reservation, ScrollPosition, Space } from 'types/common';
import { ErrorResponse } from 'types/response';
import { formatDate } from 'utils/datetime';
import * as Styled from './GuestMap.styles';

export interface GuestMapState {
  spaceId?: Space['id'];
  targetDate?: Date;
  scrollPosition?: ScrollPosition;
}

export interface URLParameter {
  sharingMapId: MapItem['sharingMapId'];
}

const GuestMap = (): JSX.Element => {
  const [modalOpen, setModalOpen] = useState(false);
  const [passwordInputModalOpen, setPasswordInputModalOpen] = useState(false);
  const [selectedReservation, setSelectedReservation] = useState<Reservation>();

  const [passwordInput, onChangePasswordInput] = useInput('');

  const history = useHistory();
  const location = useLocation<GuestMapState>();
  const { sharingMapId } = useParams<URLParameter>();

  const spaceId = location.state?.spaceId;
  const targetDate = location.state?.targetDate;

  const now = new Date();
  const todayDate = new Date(now.getFullYear(), now.getMonth(), now.getDate());

  const [map, setMap] = useState<MapItem | null>(null);
  const mapDrawing = map?.mapDrawing;
  const getMap = useGuestMap(
    { sharingMapId },
    {
      onError: () => {
        history.push('/not-found');
      },
      onSuccess: (response) => {
        const mapData = response.data;

        setMap({
          ...mapData,
          mapDrawing: JSON.parse(mapData.mapDrawing) as MapDrawing,
        });
      },
      retry: false,
    }
  );

  const [spaceList, setSpaceList] = useState<Space[]>([]);
  const [selectedSpaceId, setSelectedSpaceId] = useState<Space['id'] | null>(spaceId ?? null);
  const spaces = useMemo(() => {
    const result: { [key: string]: Space } = {};
    spaceList.forEach((item) => (result[item.id] = item));

    return result;
  }, [spaceList]);

  const getSpaces = useSpaces(
    { mapId: map?.mapId as number },
    {
      enabled: map?.mapId !== undefined,
      onSuccess: (response) => {
        const { spaces } = response.data;

        setSpaceList(spaces.map((space) => ({ ...space, area: JSON.parse(space.area) as Area })));

        if (selectedSpaceId === null) {
          setSelectedSpaceId(spaces[0].id);
        }
      },
    }
  );

  const [date, setDate] = useState(targetDate ? new Date(targetDate) : new Date());

  const getReservations = useGuestReservations(
    {
      mapId: map?.mapId as number,
      spaceId: Number(selectedSpaceId),
      date: formatDate(date),
    },
    {
      enabled: map?.mapId !== undefined && selectedSpaceId !== null,
    }
  );
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

  const handleClickSpaceArea = (spaceId: number) => {
    setSelectedSpaceId(spaceId);
  };

  const handleSelectModal = (reservation: Reservation) => {
    setModalOpen(true);
    setSelectedReservation(reservation);
  };

  const handleSelectEdit = () => {
    if (!selectedSpaceId) return;

    history.push({
      pathname: `/guest/${sharingMapId}/reservation/edit`,
      state: {
        mapId: map?.mapId,
        space: spaces[selectedSpaceId],
        reservation: selectedReservation,
        selectedDate: formatDate(date),
      },
    });
  };

  const handleSelectDelete = (): void => {
    setPasswordInputModalOpen(true);
  };

  const handleDeleteReservation: FormEventHandler<HTMLFormElement> = (event) => {
    event.preventDefault();

    if (typeof map?.mapId !== 'number' || selectedSpaceId === null) return;

    removeReservation.mutate({
      mapId: map?.mapId,
      spaceId: selectedSpaceId,
      password: passwordInput,
      reservationId: Number(selectedReservation?.id),
    });
  };

  return (
    <>
      <Header />
      <Layout>
        <Styled.PageWithBottomButton hasBottomButton={reservationAvailable}>
          <Styled.PageTitle>{map?.mapName}</Styled.PageTitle>
          <DateInput date={date} setDate={setDate} />
          <Styled.MapContainer>
            {mapDrawing && (
              <Styled.MapContainerInner width={mapDrawing.width} height={mapDrawing.height}>
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  version="1.1"
                  width={mapDrawing.width}
                  height={mapDrawing.height}
                >
                  {mapDrawing.mapElements.map((element) => (
                    <polyline
                      key={`polyline-${element.id}`}
                      points={element.points.join(' ')}
                      stroke={element.stroke}
                      strokeWidth="2"
                    />
                  ))}

                  {spaceList.length > 0 &&
                    spaceList.map(({ id, area, color, name }) => (
                      <Styled.Space key={`area-${id}`} onClick={() => handleClickSpaceArea(id)}>
                        {area.shape === 'rect' && (
                          <Styled.SpaceArea
                            x={area.x}
                            y={area.y}
                            width={area.width}
                            height={area.height}
                            fill={color ?? PALETTE.RED[200]}
                            opacity="0.3"
                          />
                        )}

                        <Styled.SpaceAreaText
                          x={area.x + area.width / 2}
                          y={area.y + area.height / 2}
                        >
                          {name}
                        </Styled.SpaceAreaText>
                      </Styled.Space>
                    ))}
                </svg>
              </Styled.MapContainerInner>
            )}
          </Styled.MapContainer>
          {spaceList.length > 0 && selectedSpaceId !== null && (
            <Styled.PanelContainer>
              <Panel>
                <Panel.Header dotColor={spaces[selectedSpaceId].color}>
                  <Panel.Title>{spaces[selectedSpaceId].name}</Panel.Title>
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
          )}
        </Styled.PageWithBottomButton>
        {spaceList.length > 0 && selectedSpaceId !== null && reservationAvailable && (
          <Styled.ReservationLink
            to={{
              pathname: `/guest/${sharingMapId}/reservation`,
              state: {
                mapId: map?.mapId,
                space: spaces[selectedSpaceId],
                selectedDate: formatDate(date),
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

export default GuestMap;
