import { AxiosError } from 'axios';
import { useEffect, useMemo, useState } from 'react';
import { useMutation } from 'react-query';
import { useHistory, useLocation } from 'react-router-dom';
import { deleteMap } from 'api/managerMap';
import { ReactComponent as DeleteIcon } from 'assets/svg/delete.svg';
import { ReactComponent as EditIcon } from 'assets/svg/edit.svg';
import { ReactComponent as MapEditorIcon } from 'assets/svg/map-editor.svg';
import { ReactComponent as MenuIcon } from 'assets/svg/menu.svg';
import { ReactComponent as MoreIcon } from 'assets/svg/more.svg';
import { ReactComponent as SpaceEditorIcon } from 'assets/svg/space-editor.svg';
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
import useManagerMaps from 'hooks/useManagerMaps';
import useManagerReservations from 'hooks/useManagerReservations';
import { ErrorResponse, MapItemResponse } from 'types/response';
import { formatDate } from 'utils/datetime';
import { isNullish } from 'utils/type';
import * as Styled from './ManagerMain.styles';

interface LocationState {
  mapId?: number;
}

const ManagerMain = (): JSX.Element => {
  const history = useHistory();
  const location = useLocation<LocationState>();

  const [date, setDate] = useState(new Date());
  const [open, setOpen] = useState(false);

  const [selectedMapId, setSelectedMapId] = useState<number | null>(location.state?.mapId ?? null);
  const [selectedMapName, setSelectedMapName] = useState('');

  const onRequestError = (error: AxiosError<ErrorResponse>) => {
    alert(error.response?.data?.message ?? MESSAGE.MANAGER_MAIN.UNEXPECTED_GET_DATA_ERROR);
  };

  const getMaps = useManagerMaps({
    onError: onRequestError,
  });

  const organization = getMaps.data?.data.organization ?? '';
  const maps = useMemo((): MapItemResponse[] => getMaps.data?.data.maps ?? [], [getMaps]);

  const getReservations = useManagerReservations(
    {
      mapId: selectedMapId,
      date: formatDate(date),
    },
    {
      enabled: !(selectedMapId === null),
      onError: onRequestError,
    }
  );

  const reservations = getReservations.data?.data?.data ?? [];

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

        <Styled.SpaceList>
          {reservations &&
            reservations.map(({ spaceId, spaceName, spaceColor, reservations }, index) => (
              <Styled.SpaceReservationWrapper key={`space-${spaceId}`}>
                <Panel expandable initialExpanded={!index}>
                  <Panel.Header dotColor={spaceColor}>
                    <Panel.Title>{spaceName}</Panel.Title>
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
                              <IconButton>
                                <MoreIcon width="100%" height="100%" />
                              </IconButton>
                            }
                          />
                        ))}
                      </>
                    )}
                  </Panel.Content>
                </Panel>
              </Styled.SpaceReservationWrapper>
            ))}
        </Styled.SpaceList>
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
