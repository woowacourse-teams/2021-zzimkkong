import { AxiosError } from 'axios';
import { useEffect, useMemo, useState } from 'react';
import { useMutation } from 'react-query';
import { useHistory } from 'react-router-dom';
import { deleteMap } from 'api/managerMap';
import { ReactComponent as DeleteIcon } from 'assets/svg/delete.svg';
import { ReactComponent as EditIcon } from 'assets/svg/edit.svg';
import { ReactComponent as MenuIcon } from 'assets/svg/menu.svg';
import { ReactComponent as MoreIcon } from 'assets/svg/more.svg';
import DateInput from 'components/DateInput/DateInput';
import Drawer from 'components/Drawer/Drawer';
import Header from 'components/Header/Header';
import IconButton from 'components/IconButton/IconButton';
import Layout from 'components/Layout/Layout';
import MapListItem from 'components/MapListItem/MapListItem';
import Panel from 'components/Panel/Panel';
import ReservationListItem from 'components/ReservationListItem/ReservationListItem';
import MESSAGE from 'constants/message';
import PATH, { HREF } from 'constants/path';
import useManagerMaps from 'hooks/useManagerMaps';
import useManagerReservations from 'hooks/useManagerReservations';
import { ErrorResponse, MapItemResponse } from 'types/response';
import { formatDate } from 'utils/datetime';
import * as Styled from './ManagerMain.styles';

const ManagerMain = (): JSX.Element => {
  const history = useHistory();

  const [date, setDate] = useState(new Date());
  const [open, setOpen] = useState(false);
  const [selectedMapId, setSelectedMapId] = useState<number | null>(null);
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
      enabled: selectedMapId ? true : false,
      onError: onRequestError,
    }
  );

  const reservations = getReservations.data?.data?.data ?? [];

  const removeMap = useMutation(deleteMap, {
    onSuccess: () => {
      alert('맵이 삭제 되었습니다.');
    },

    onError: (error: AxiosError<ErrorResponse>) => {
      alert(error.response?.data.message ?? MESSAGE.MANAGER_MAIN.UNEXPECTED_MAP_DELETE_ERROR);
    },
  });

  const handleDeleteMap = (mapId: number) => {
    removeMap.mutate({ mapId });
  };

  const getSelectedPublicMapId = () => {
    const selectedMap = maps.find((map) => map.mapId === selectedMapId);

    return selectedMap?.publicMapId ?? '';
  };

  const handleCopyLink = () => {
    navigator.clipboard
      .writeText(`${window.location.origin}/guest/${getSelectedPublicMapId()}`)
      .then(
        () => {
          alert('맵의 공유링크가 클립보드에 복사되었습니다!');
        },
        () => {
          alert('공유링크를 복사하는데 문제가 발생했습니다.');
        }
      );
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
    setSelectedMapId(maps.length ? maps[0].mapId : null);
    setSelectedMapName(maps.length ? maps[0].mapName : '');
  }, [maps]);

  return (
    <>
      <Header />
      <Layout>
        <Styled.PageHeader>
          <IconButton text="맵 목록" onClick={handleOpenDrawer}>
            <MenuIcon width="100%" height="100%" />
          </IconButton>
          <Styled.PageTitle>{selectedMapName}</Styled.PageTitle>
          <IconButton text="공유 링크" onClick={handleCopyLink}>
            <Styled.PrimaryLinkIcon width="100%" height="100%" />
          </IconButton>
        </Styled.PageHeader>
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
              {/* 공간 편집 페이지 완성되면 링크 바꿔야 함 */}
              <Styled.NoticeLink to={PATH.MANAGER_MAP_CREATE}>공간 생성하러 가기</Styled.NoticeLink>
            </Styled.NoticeWrapper>
          ))}

        <Styled.SpaceList>
          {reservations &&
            reservations.map(({ id, name, color, reservations }, index) => (
              <Styled.SpaceReservationWrapper key={`space-${id}`}>
                <Panel expandable initialExpanded={!index}>
                  <Panel.Header dotColor={color}>
                    <Panel.Title>{name}</Panel.Title>
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
                    <Styled.MapListItemControlButton
                      size="small"
                      onClick={() => history.push(HREF.MANAGER_MAP_EDIT(mapId))}
                    >
                      <EditIcon width="100%" height="100%" />
                    </Styled.MapListItemControlButton>
                    <Styled.MapListItemControlButton
                      size="small"
                      onClick={() => handleDeleteMap(mapId)}
                    >
                      <DeleteIcon width="100%" height="100%" />
                    </Styled.MapListItemControlButton>
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
