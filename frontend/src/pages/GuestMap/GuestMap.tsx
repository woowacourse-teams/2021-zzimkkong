import { AxiosError } from 'axios';
import dayjs, { Dayjs } from 'dayjs';
import React, { useContext, useEffect, useMemo, useRef, useState } from 'react';
import { useMutation } from 'react-query';
import { useHistory, useLocation, useParams } from 'react-router-dom';
import { deleteGuestReservation } from 'api/guestReservation';
import DateInput from 'components/DateInput/DateInput';
import Header from 'components/Header/Header';
import Layout from 'components/Layout/Layout';
import { EDITOR } from 'constants/editor';
import MESSAGE from 'constants/message';
import PALETTE from 'constants/palette';
import PATH, { HREF } from 'constants/path';
import useGuestMap from 'hooks/query/useGuestMap';
import useGuestReservations from 'hooks/query/useGuestReservations';
import useGuestSpaces from 'hooks/query/useGuestSpaces';
import { AccessTokenContext } from 'providers/AccessTokenProvider';
import { Area, MapDrawing, MapItem, Reservation, ScrollPosition, Space } from 'types/common';
import { DrawingAreaShape } from 'types/editor';
import { GuestPageURLParams } from 'types/guest';
import { ErrorResponse } from 'types/response';
import { formatDate } from 'utils/datetime';
import { getPolygonCenterPoint } from 'utils/editor';
import { isNullish } from 'utils/type';
import * as Styled from './GuestMap.styles';
import LoginPopup from './units/LoginPopup';
import PasswordInputModal from './units/PasswordInputModal';
import ReservationDrawer from './units/ReservationDrawer';

export interface GuestMapState {
  spaceId?: Space['id'];
  targetDate?: Dayjs;
  scrollPosition?: ScrollPosition;
}

const GuestMap = (): JSX.Element => {
  const { accessToken } = useContext(AccessTokenContext);

  const [detailOpen, setDetailOpen] = useState(false);
  const [passwordInputModalOpen, setPasswordInputModalOpen] = useState(false);
  const [selectedReservation, setSelectedReservation] = useState<Reservation>();

  const [loginPopupOpen, setLoginPopupOpen] = useState(true);

  const history = useHistory();
  const location = useLocation<GuestMapState>();
  const { sharingMapId } = useParams<GuestPageURLParams>();

  const mapRef = useRef<HTMLDivElement | null>(null);

  const spaceId = location.state?.spaceId;
  const targetDate = location.state?.targetDate;
  const scrollPosition = location.state?.scrollPosition;

  const [map, setMap] = useState<MapItem | null>(null);
  const mapDrawing = map?.mapDrawing;
  useGuestMap(
    { sharingMapId },
    {
      onError: () => {
        history.replace(PATH.NOT_FOUND);
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
  const [date, setDate] = useState(targetDate ? dayjs(targetDate).tz() : dayjs().tz());

  const spaces = useMemo(() => {
    const result: { [key: string]: Space } = {};
    spaceList.forEach((item) => (result[item.id] = item));

    return result;
  }, [spaceList]);

  useGuestSpaces(
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

  const getReservations = useGuestReservations(
    {
      mapId: map?.mapId as number,
      spaceId: selectedSpaceId as number,
      date: formatDate(date),
    },
    {
      enabled: !isNullish(selectedSpaceId),
    }
  );

  const reservations = getReservations.data?.data?.reservations ?? [];

  const removeReservation = useMutation(deleteGuestReservation, {
    onSuccess: () => {
      getReservations.refetch();
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
      pathname: HREF.GUEST_RESERVATION_EDIT(sharingMapId),
      state: {
        mapId: map?.mapId,
        space: spaces[selectedSpaceId],
        reservation,
        selectedDate: formatDate(date),
        scrollPosition: { x: mapRef?.current?.scrollLeft, y: mapRef?.current?.scrollTop },
      },
    });
  };

  const deleteLoginReservation = (reservationId: number) => {
    if (typeof map?.mapId !== 'number' || selectedSpaceId === null) return;

    if (!window.confirm(MESSAGE.GUEST_MAP.DELETE_CONFIRM)) return;

    removeReservation.mutate({
      mapId: map?.mapId,
      spaceId: selectedSpaceId,
      reservationId: reservationId,
    });
  };

  const handleDeleteGuestReservation = (passwordInput: string) => {
    if (typeof map?.mapId !== 'number' || selectedSpaceId === null) return;

    removeReservation.mutate({
      mapId: map?.mapId,
      spaceId: selectedSpaceId,
      password: passwordInput,
      reservationId: Number(selectedReservation?.id),
    });
  };

  const handleDelete = (reservation: Reservation) => {
    if (!reservation.isLoginReservation) {
      setPasswordInputModalOpen(true);
      setSelectedReservation(reservation);
    } else {
      deleteLoginReservation(reservation.id);
    }
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

  const handleLogin = () => {
    getReservations.refetch();
  };

  useEffect(() => {
    if (scrollPosition) {
      mapRef.current?.scrollTo(scrollPosition.x ?? 0, scrollPosition.y ?? 0);
    }
  }, [scrollPosition]);

  useEffect(() => {
    if (targetDate) {
      setDate(dayjs(targetDate).tz());
    }
  }, [targetDate]);

  return (
    <>
      <Header onClickLogin={() => setLoginPopupOpen(true)} />
      <Layout>
        <Styled.Page>
          <Styled.PageHeader>
            <Styled.MapInfo>
              <Styled.PageTitle>{map?.mapName}</Styled.PageTitle>
              <DateInput date={date} setDate={setDate} hasBackground={false} />
            </Styled.MapInfo>
            {map?.notice && (
              <Styled.NoticeWrapper>
                <Styled.Notice>
                  <Styled.NoticeTitle>공지사항</Styled.NoticeTitle>
                  <Styled.NoticeText>{map?.notice ?? ''}</Styled.NoticeText>
                </Styled.Notice>
              </Styled.NoticeWrapper>
            )}
          </Styled.PageHeader>
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
                        {area.shape === DrawingAreaShape.Rect && (
                          <>
                            <Styled.SpaceRect
                              x={area.x}
                              y={area.y}
                              width={area.width}
                              height={area.height}
                              fill={color ?? PALETTE.RED[200]}
                              opacity="0.3"
                            />
                            <Styled.SpaceAreaText
                              x={area.x + area.width / 2}
                              y={area.y + area.height / 2}
                            >
                              {name}
                            </Styled.SpaceAreaText>
                          </>
                        )}
                        {area.shape === DrawingAreaShape.Polygon && (
                          <>
                            <Styled.SpacePolygon
                              points={area.points.map(({ x, y }) => `${x},${y}`).join(' ')}
                              fill={color ?? PALETTE.RED[200]}
                              opacity="0.3"
                            />
                            <Styled.SpaceAreaText
                              x={getPolygonCenterPoint(area.points).x}
                              y={getPolygonCenterPoint(area.points).y}
                            >
                              {name}
                            </Styled.SpaceAreaText>
                          </>
                        )}
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
          reservations={reservations}
          space={spaces[selectedSpaceId]}
          date={date}
          open={detailOpen}
          isSuccess={getReservations.isSuccess}
          isLoadingError={getReservations.isLoadingError}
          onClose={() => setDetailOpen(false)}
          onClickReservation={handleReservation}
          onEdit={handleEdit}
          onDelete={handleDelete}
        />
      )}
      <PasswordInputModal
        open={passwordInputModalOpen}
        onClose={() => setPasswordInputModalOpen(false)}
        onSubmit={handleDeleteGuestReservation}
      />

      {!accessToken && (
        <LoginPopup
          open={loginPopupOpen}
          onClose={() => setLoginPopupOpen(false)}
          onLogin={handleLogin}
        />
      )}
    </>
  );
};

export default GuestMap;
