import { AxiosError } from 'axios';
import { useEffect, useState } from 'react';
import { useMutation } from 'react-query';
import { useHistory } from 'react-router-dom';
import { deleteMap } from 'api/map';
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
import PATH from 'constants/path';
import { LOCAL_STORAGE_KEY } from 'constants/storage';
import useManagerMaps from 'hooks/useManagerMaps';
import useManagerReservations from 'hooks/useManagerReservations';
import { SpaceReservation } from 'types/common';
import { ErrorResponse } from 'types/response';
import { formatDate } from 'utils/datetime';
import * as Styled from './ManagerMain.styles';

/*
 공간 관리자 메인 페이지에서 맵 ID가 고정되는게 아닌 공간관리자 별로 맵 목록을 조회해서 가져올 수 있도록 수정
  - 페이지 접속시 먼저 맵 목록 조회
    - 맵이 있다면 -> 최상단의 맵 예약 정보를 화면에 보여줌
    - 맵이 없다면 -> 화면에 '맵을 먼저 생성해주세요' 메세지 표시

 - 맵 목록 중 선택 된 맵을 표시하는 기능 - border 색상 변경 등
*/

const ManagerMain = (): JSX.Element => {
  const history = useHistory();

  const [date, setDate] = useState(new Date());
  const [open, setOpen] = useState(false);

  const onRequestError = (error: AxiosError<ErrorResponse>) => {
    alert(error.response?.data?.message ?? MESSAGE.MANAGER_MAIN.UNEXPECTED_GET_DATA_ERROR);

    if (error.response?.status === 401) {
      localStorage.removeItem(LOCAL_STORAGE_KEY.ACCESS_TOKEN);
      history.push(PATH.MANAGER_LOGIN);
    }
  };

  const getMaps = useManagerMaps({
    onError: onRequestError,
  });

  const organization = getMaps.data?.data.organization ?? '';

  const maps = getMaps.data?.data.maps ?? [];
  // const mapId = maps.length ? maps[0].mapId : 0;
  const [selectedMapId, setSelectedMapId] = useState(0);
  const mapName = maps.length ? maps[0].mapName : '';

  useEffect(() => {
    setSelectedMapId(maps.length ? maps[0].mapId : 0);
  }, [maps]);

  const getReservations = useManagerReservations(
    {
      mapId: selectedMapId,
      date: formatDate(date),
    },
    {
      enabled: maps.length ? true : false,
      onError: onRequestError,
    }
  );

  const reservations = getReservations.data?.data?.data ?? [];

  // const [reservations, setReservations] = useState<SpaceReservation[]>([]);

  // useEffect((): void => {
  //   // getReservations.data?.data?.data ?? []

  //   setReservations(getReservations.data?.data?.data ?? []);
  // }, [mapId]);

  // useEffect((): void => {
  //   setReservations(getReservations.data?.data?.data ?? []);
  // }, []);

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

  const onOpenDrawer = () => {
    setOpen(true);
  };

  const onCloseDrawer = () => {
    setOpen(false);
  };

  const handleSelectMap = (mapId: number) => {
    setSelectedMapId(mapId);
    onCloseDrawer();
  };

  return (
    <>
      <Header />
      <Layout>
        <Styled.PageHeader>
          <IconButton text="맵 목록" onClick={onOpenDrawer}>
            <MenuIcon width="100%" height="100%" />
          </IconButton>
          <Styled.PageTitle>{mapName}</Styled.PageTitle>
          <IconButton text="공유 링크">
            <Styled.PrimaryLinkIcon width="100%" height="100%" />
          </IconButton>
        </Styled.PageHeader>
        <Styled.DateInputWrapper>
          <DateInput date={date} setDate={setDate} />
        </Styled.DateInputWrapper>

        {!reservations.length && selectedMapId && (
          <Styled.CreateMapWrapper>
            <Styled.CreateMapMessage>생성한 공간이 없습니다.</Styled.CreateMapMessage>
            {/* 공간 편집 페이지 완성되면 링크 바꿔야 함 */}
            <Styled.CreateMapLink to={PATH.MANAGER_MAP_CREATE}>
              공간 생성하러 가기
            </Styled.CreateMapLink>
          </Styled.CreateMapWrapper>
        )}

        {!reservations.length && !selectedMapId && (
          <Styled.CreateMapWrapper>
            <Styled.CreateMapMessage>생성한 맵이 없습니다.</Styled.CreateMapMessage>
            <Styled.CreateMapLink to={PATH.MANAGER_MAP_CREATE}>
              맵 생성하러 가기
            </Styled.CreateMapLink>
          </Styled.CreateMapWrapper>
        )}

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

      <Drawer open={open} placement="left" maxwidth="450px" onClose={onCloseDrawer}>
        <Drawer.Inner>
          <Drawer.Header>
            <Drawer.HeaderText>{organization}</Drawer.HeaderText>
            <Drawer.CloseButton />
          </Drawer.Header>
          {maps.map(({ mapId, mapName, mapImageUrl }) => (
            <Styled.SpaceWrapper key={`map-${mapId}`}>
              <MapListItem
                onClick={() => handleSelectMap(mapId)}
                thumbnail={{ src: mapImageUrl, alt: mapName }}
                title={mapName}
                selected={mapId === selectedMapId}
                control={
                  <>
                    <Styled.MapListItemControlButton size="small">
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
