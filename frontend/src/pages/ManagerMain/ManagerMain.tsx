import { AxiosError } from 'axios';
import { useEffect, useMemo, useState } from 'react';
import { useMutation } from 'react-query';
import { useHistory, useLocation } from 'react-router-dom';
import { deleteMap } from 'api/managerMap';
import { deleteManagerReservation } from 'api/managerReservation';
import { ReactComponent as DeleteIcon } from 'assets/svg/delete.svg';
import { ReactComponent as EditIcon } from 'assets/svg/edit.svg';
import { ReactComponent as MapEditorIcon } from 'assets/svg/map-editor.svg';
import { ReactComponent as MenuIcon } from 'assets/svg/menu.svg';
import { ReactComponent as SpaceEditorIcon } from 'assets/svg/space-editor.svg';
import Button from 'components/Button/Button';
import DateInput from 'components/DateInput/DateInput';
import Drawer from 'components/Drawer/Drawer';
import Header from 'components/Header/Header';
import IconButton from 'components/IconButton/IconButton';
import Layout from 'components/Layout/Layout';
import MapListItem from 'components/MapListItem/MapListItem';
import PageHeader from 'components/PageHeader/PageHeader';
import Panel from 'components/Panel/Panel';
import ReservationListItem from 'components/ReservationListItem/ReservationListItem';
import MESSAGE from 'constants/message';
import PATH, { HREF } from 'constants/path';
import useManagerMapReservations from 'hooks/query/useManagerMapReservations';
import useManagerMaps from 'hooks/query/useManagerMaps';
import useManagerSpaces from 'hooks/query/useManagerSpaces';
import { Order, Reservation } from 'types/common';
import { ErrorResponse, MapItemResponse } from 'types/response';
import { formatDate } from 'utils/datetime';
import { sortReservations } from 'utils/sort';
import { isNullish } from 'utils/type';
import * as Styled from './ManagerMain.styles';

export interface ManagerMainState {
  mapId?: number;
  targetDate?: Date;
}

const ManagerMain = (): JSX.Element => {
  const history = useHistory();
  const location = useLocation<ManagerMainState>();

  const mapId = location.state?.mapId;
  const targetDate = location.state?.targetDate;

  const [date, setDate] = useState(targetDate ?? new Date());
  const [open, setOpen] = useState(false);

  const [selectedMapId, setSelectedMapId] = useState<number | null>(mapId ?? null);
  const [selectedMapName, setSelectedMapName] = useState('');
  const [spacesOrder, setSpacesOrder] = useState<Order>(Order.Ascending);

  const onRequestError = (error: AxiosError<ErrorResponse>) => {
    alert(error.response?.data?.message ?? MESSAGE.MANAGER_MAIN.UNEXPECTED_GET_DATA_ERROR);
  };

  const getMaps = useManagerMaps({
    onError: onRequestError,
  });

  const organization = getMaps.data?.data.organization ?? '';
  const maps = useMemo((): MapItemResponse[] => getMaps.data?.data.maps ?? [], [getMaps]);

  const getReservations = useManagerMapReservations(
    {
      mapId: selectedMapId,
      date: formatDate(date),
    },
    {
      enabled: !isNullish(selectedMapId),
      onError: onRequestError,
    }
  );

  const getSpaces = useManagerSpaces(
    {
      mapId: selectedMapId,
    },
    {
      enabled: !isNullish(selectedMapId),
    }
  );

  const removeReservation = useMutation(deleteManagerReservation, {
    onSuccess: () => {
      alert(MESSAGE.MANAGER_MAIN.RESERVATION_DELETE);
    },

    onError: (error: AxiosError<ErrorResponse>) => {
      alert(error.response?.data.message ?? MESSAGE.RESERVATION.UNEXPECTED_DELETE_ERROR);
    },
  });

  const reservations = useMemo(() => getReservations.data?.data?.data ?? [], [getReservations]);
  const sortedReservations = useMemo(
    () => sortReservations(reservations, spacesOrder),
    [reservations, spacesOrder]
  );

  const spaces = useMemo(() => getSpaces.data?.data.spaces ?? [], [getSpaces]);

  const handleClickSpacesOrder = () => {
    setSpacesOrder((prev) => (prev === Order.Ascending ? Order.Descending : Order.Ascending));
  };

  const removeMap = useMutation(deleteMap, {
    onSuccess: () => {
      alert(MESSAGE.MANAGER_MAIN.MAP_DELETED);
    },

    onError: (error: AxiosError<ErrorResponse>) => {
      alert(error.response?.data.message ?? MESSAGE.MANAGER_MAIN.UNEXPECTED_MAP_DELETE_ERROR);
    },
  });

  const handleDeleteMap = (mapId: number) => {
    removeMap.mutate({ mapId });
  };

  const getSelectedSharingMapId = () => {
    const selectedMap = maps.find((map) => map.mapId === selectedMapId);

    return selectedMap?.sharingMapId ?? '';
  };

  const handleClickMapEditorIcon = () => {
    if (selectedMapId === null) return;

    history.push(HREF.MANAGER_MAP_EDIT(selectedMapId));
  };

  const handleClickSpaceEditorIcon = () => {
    if (selectedMapId === null) return;

    history.push(HREF.MANAGER_SPACE_EDIT(selectedMapId));
  };

  const handleCopyLink = () => {
    if (selectedMapId === null) {
      alert(MESSAGE.MANAGER_MAIN.SELECT_MAP);

      return;
    }

    navigator.clipboard
      .writeText(`${window.location.origin}/guest/${getSelectedSharingMapId()}`)
      .then(() => {
        alert(MESSAGE.MANAGER_MAIN.COPIED_SHARE_LINK);
      })
      .catch(() => {
        alert(MESSAGE.MANAGER_MAIN.UNEXPECTED_COPY_SHARE_LINK);
      });
  };

  const handleOpenDrawer = () => {
    setOpen(true);
  };

  const handleCloseDrawer = () => {
    setOpen(false);
  };

  const handleSelectMap = (mapId: number, mapName: string) => {
    setSelectedMapId(mapId);
    setSelectedMapName(mapName);
    handleCloseDrawer();
  };

  const handleCreateReservation = (spaceId: number) => {
    if (!selectedMapId) return;

    history.push({
      pathname: PATH.MANAGER_RESERVATION,
      state: {
        mapId: selectedMapId,
        space: spaces?.find(({ id }) => id === spaceId),
        selectedDate: formatDate(date),
      },
    });
  };

  const handleEditReservation = (reservation: Reservation, spaceId: number) => {
    if (!selectedMapId) return;

    history.push({
      pathname: PATH.MANAGER_RESERVATION_EDIT,
      state: {
        mapId: selectedMapId,
        spaceId,
        reservation,
        selectedDate: formatDate(date),
      },
    });
  };

  const handleDeleteReservation = (reservationId: number, spaceId: number) => {
    if (!selectedMapId) return;

    if (!window.confirm(MESSAGE.MANAGER_MAIN.RESERVATION_DELETE_CONFIRM)) return;

    removeReservation.mutate({
      mapId: selectedMapId,
      spaceId,
      reservationId,
    });
  };

  useEffect(() => {
    const prevMapId = location.state?.mapId ?? null;
    const prevMapName = maps.find(({ mapId }) => mapId === prevMapId)?.mapName ?? '';

    if (isNullish(prevMapId)) {
      setSelectedMapId(maps.length ? maps[0].mapId : null);
      setSelectedMapName(maps.length ? maps[0].mapName : '');

      return;
    }

    setSelectedMapId(prevMapId);
    setSelectedMapName(prevMapName);
  }, [location.state?.mapId, maps]);

  return (
    <>
      <Header />
      <Layout>
        <PageHeader
          title={selectedMapName}
          leftButtons={
            <IconButton text="맵 목록" size="small" onClick={handleOpenDrawer}>
              <MenuIcon width="100%" height="100%" />
            </IconButton>
          }
          rightButtons={
            selectedMapId !== null && (
              <>
                <Styled.RightIconButton
                  text="맵 편집"
                  size="small"
                  onClick={handleClickMapEditorIcon}
                >
                  <MapEditorIcon width="100%" height="100%" />
                </Styled.RightIconButton>
                <Styled.RightIconButton
                  text="공간 편집"
                  size="small"
                  onClick={handleClickSpaceEditorIcon}
                >
                  <SpaceEditorIcon width="100%" height="100%" />
                </Styled.RightIconButton>
                <Styled.VerticalBar />
                <Styled.RightIconButton text="공유 링크" size="small" onClick={handleCopyLink}>
                  <Styled.PrimaryLinkIcon width="100%" height="100%" />
                </Styled.RightIconButton>
              </>
            )
          }
        />
        <Styled.DateInputWrapper>
          <DateInput date={date} setDate={setDate} />
        </Styled.DateInputWrapper>

        {getReservations.isLoading && (
          <Styled.NoticeWrapper>
            <Styled.NoticeMessage>공간을 로딩 중입니다</Styled.NoticeMessage>
          </Styled.NoticeWrapper>
        )}

        {!getReservations.isLoading &&
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
          <Styled.SpacesOrderButton variant="text" onClick={handleClickSpacesOrder}>
            {spacesOrder === 'ascending' ? '오름차순 △' : '내림차순 ▽'}
          </Styled.SpacesOrderButton>
          <Styled.SpaceList>
            {sortedReservations &&
              sortedReservations.map(({ spaceId, spaceName, spaceColor, reservations }, index) => (
                <Panel key={`space-${spaceId}`} role="listitem">
                  <Panel.Header dotColor={spaceColor}>
                    <Styled.PanelHeadWrapper>
                      <Panel.Title>{spaceName}</Panel.Title>
                      <Button
                        variant="primary-text"
                        size="dense"
                        onClick={() => handleCreateReservation(spaceId)}
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
                          <ReservationListItem
                            key={`reservation-${reservation.id}`}
                            reservation={reservation}
                            control={
                              <Styled.IconButtonWrapper>
                                <IconButton
                                  size="small"
                                  onClick={() => handleEditReservation(reservation, spaceId)}
                                >
                                  <EditIcon width="100%" height="100%" />
                                </IconButton>

                                <IconButton
                                  size="small"
                                  onClick={() => handleDeleteReservation(reservation.id, spaceId)}
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
      </Layout>

      <Drawer open={open} placement="left" maxwidth="450px" onClose={handleCloseDrawer}>
        <Drawer.Inner>
          <Drawer.Header>
            <Drawer.HeaderText>{organization}</Drawer.HeaderText>
            <Drawer.CloseButton />
          </Drawer.Header>
          {maps.map(({ mapId, mapName, mapImageUrl }) => (
            <Styled.SpaceWrapper key={`map-${mapId}`}>
              <MapListItem
                onClick={() => handleSelectMap(mapId, mapName)}
                thumbnail={{ src: mapImageUrl, alt: mapName }}
                title={mapName}
                selected={mapId === selectedMapId}
                control={
                  <>
                    <IconButton size="small">
                      <EditIcon width="100%" height="100%" />
                    </IconButton>
                    <IconButton size="small" onClick={() => handleDeleteMap(mapId)}>
                      <DeleteIcon width="100%" height="100%" />
                    </IconButton>
                  </>
                }
              />
            </Styled.SpaceWrapper>
          ))}

          <Styled.CreateMapButton to={PATH.MANAGER_MAP_CREATE}>
            <Styled.PlusIcon width="100%" height="100%" />
          </Styled.CreateMapButton>
        </Drawer.Inner>
      </Drawer>
    </>
  );
};

export default ManagerMain;
