import { AxiosError } from 'axios';
import { FormEventHandler, useEffect, useMemo, useRef, useState } from 'react';
import { useMutation } from 'react-query';
import { useHistory, useLocation, useParams } from 'react-router-dom';
import { deleteGuestReservation } from 'api/guestReservation';
import Button from 'components/Button/Button';
import DateInput from 'components/DateInput/DateInput';
import Header from 'components/Header/Header';
import Input from 'components/Input/Input';
import Layout from 'components/Layout/Layout';
import Modal from 'components/Modal/Modal';
import { EDITOR } from 'constants/editor';
import MESSAGE from 'constants/message';
import PALETTE from 'constants/palette';
import PATH, { HREF } from 'constants/path';
import useGuestMap from 'hooks/query/useGuestMap';
import useGuestSpaces from 'hooks/query/useGuestSpaces';
import useInput from 'hooks/useInput';
import { Area, MapDrawing, MapItem, Reservation, ScrollPosition, Space } from 'types/common';
import { ErrorResponse } from 'types/response';
import { formatDate } from 'utils/datetime';
import * as Styled from './GuestMap.styles';
import ReservationDrawer from './units/ReservationDrawer';

export interface GuestMapState {
  spaceId?: Space['id'];
  targetDate?: Date;
  scrollPosition?: ScrollPosition;
}

export interface URLParameter {
  sharingMapId: MapItem['sharingMapId'];
}

const GuestMap = (): JSX.Element => {
  const [detailOpen, setDetailOpen] = useState(false);
  const [passwordInputModalOpen, setPasswordInputModalOpen] = useState(false);
  const [selectedReservation, setSelectedReservation] = useState<Reservation>();

  const [passwordInput, onChangePasswordInput] = useInput('');

  const history = useHistory();
  const location = useLocation<GuestMapState>();
  const { sharingMapId } = useParams<URLParameter>();

  const mapRef = useRef<HTMLDivElement | null>(null);

  const spaceId = location.state?.spaceId;
  const targetDate = location.state?.targetDate;
  const scrollPosition = location.state?.scrollPosition;

  const [map, setMap] = useState<MapItem | null>(null);
  const mapDrawing = map?.mapDrawing;
  const getMap = useGuestMap(
    { sharingMapId },
    {
      onError: () => {
        history.push(PATH.NOT_FOUND[1]);
      },
      onSuccess: (response) => {
        const mapData = response.data;

        try {
          setMap({
            ...mapData,
            mapDrawing: JSON.parse(mapData.mapDrawing) as MapDrawing,
          });
        } catch (error) {
          alert(MESSAGE.GUEST_MAP.MAP_DRAWING_PARSE_ERROR);
        }
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

  const getSpaces = useGuestSpaces(
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

  const removeReservation = useMutation(deleteGuestReservation, {
    onSuccess: () => {
      window.alert(MESSAGE.RESERVATION.DELETE_SUCCESS);
      setPasswordInputModalOpen(false);
    },

    onError: (error: AxiosError<ErrorResponse>) => {
      alert(error.response?.data.message ?? MESSAGE.RESERVATION.UNEXPECTED_DELETE_ERROR);
    },
  });

  const handleClickSpaceArea = (spaceId: number) => {
    setSelectedSpaceId(spaceId);
    setDetailOpen(true);
  };

  const handleEdit = (reservation: Reservation) => {
    if (!selectedSpaceId) return;

    history.push({
      pathname: `/guest/${sharingMapId}/reservation/edit`,
      state: {
        mapId: map?.mapId,
        space: spaces[selectedSpaceId],
        reservation,
        selectedDate: formatDate(date),
        scrollPosition: { x: mapRef?.current?.scrollLeft, y: mapRef?.current?.scrollTop },
      },
    });
  };

  const handleDelete = (reservation: Reservation) => {
    setPasswordInputModalOpen(true);
    setSelectedReservation(reservation);
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

  const handleReservation = () => {
    if (!selectedSpaceId) return;

    history.push({
      pathname: HREF.GUEST_RESERVATION(sharingMapId),
      state: {
        mapId: map?.mapId,
        space: spaces[selectedSpaceId],
        selectedDate: formatDate(date),
        scrollPosition: { x: mapRef?.current?.scrollLeft, y: mapRef?.current?.scrollTop },
      },
    });
  };

  useEffect(() => {
    if (scrollPosition) {
      mapRef.current?.scrollTo(scrollPosition.x ?? 0, scrollPosition.y ?? 0);
    }

    if (targetDate) {
      setDate(new Date(targetDate));
    }
  }, [scrollPosition, targetDate]);

  return (
    <>
      <Header />
      <Layout>
        <Styled.Page>
          <Styled.PageTitle>{map?.mapName}</Styled.PageTitle>
          <DateInput date={date} setDate={setDate} />
          <Styled.MapContainer ref={mapRef}>
            {mapDrawing && (
              <Styled.MapContainerInner width={mapDrawing.width} height={mapDrawing.height}>
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  version="1.1"
                  width={mapDrawing.width}
                  height={mapDrawing.height}
                >
                  {/* Note: 맵을 그리는 부분 */}
                  {mapDrawing.mapElements.map((element) =>
                    element.type === 'polyline' ? (
                      <polyline
                        key={`polyline-${element.id}`}
                        points={element.points.join(' ')}
                        stroke={element.stroke}
                        strokeWidth={EDITOR.STROKE_WIDTH}
                        strokeLinecap="round"
                      />
                    ) : (
                      <rect
                        key={`rect-${element.id}`}
                        x={element?.x}
                        y={element?.y}
                        width={element?.width}
                        height={element?.height}
                        stroke={element.stroke}
                        fill="none"
                        strokeWidth={EDITOR.STROKE_WIDTH}
                      />
                    )
                  )}

                  {/* Note: 공간을 그리는 부분 */}
                  {spaceList.length > 0 &&
                    spaceList.map(({ id, area, color, name }) => (
                      <Styled.Space
                        key={`area-${id}`}
                        data-testid={id}
                        onClick={() => handleClickSpaceArea(id)}
                      >
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
        </Styled.Page>
      </Layout>

      {selectedSpaceId && map?.mapId && detailOpen && (
        <ReservationDrawer
          mapId={map.mapId}
          space={spaces[selectedSpaceId]}
          date={date}
          open={detailOpen}
          onClose={() => setDetailOpen(false)}
          onClickReservation={handleReservation}
          onEdit={handleEdit}
          onDelete={handleDelete}
        />
      )}

      {passwordInputModalOpen && (
        <Modal
          open={passwordInputModalOpen}
          isClosableDimmer={true}
          onClose={() => setPasswordInputModalOpen(false)}
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
                <Button
                  variant="text"
                  type="button"
                  onClick={() => setPasswordInputModalOpen(false)}
                >
                  취소
                </Button>
                <Button variant="text" type="submit">
                  확인
                </Button>
              </Styled.DeleteModalContainer>
            </form>
          </Modal.Inner>
        </Modal>
      )}
    </>
  );
};

export default GuestMap;
