import { AxiosError } from 'axios';
import { useState } from 'react';
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
import { ErrorResponse } from 'types/response';
import { formatDate } from 'utils/datetime';
import * as Styled from './ManagerMain.styles';

const ManagerMain = (): JSX.Element => {
  const mapId = 1;
  const [date, setDate] = useState(new Date());
  const [open, setOpen] = useState(false);

  const history = useHistory();

  const onRequestError = (error: AxiosError<ErrorResponse>) => {
    alert(error.response?.data?.message ?? MESSAGE.MANAGER_MAIN.UNEXPECTED_GET_DATA_ERROR);

    if (error.response?.status === 401) {
      localStorage.removeItem(LOCAL_STORAGE_KEY.ACCESS_TOKEN);
      history.push(PATH.MANAGER_LOGIN);
    }
  };

  const mapName = '우테코 교육장';
  const getMaps = useManagerMaps({ onError: onRequestError });
  const maps = getMaps.data?.data.maps ?? [];

  const getReservations = useManagerReservations(
    {
      mapId,
      date: formatDate(date),
    },
    { onError: onRequestError }
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

  const onOpenDrawer = () => {
    setOpen(true);
  };

  const onCloseDrawer = () => {
    setOpen(false);
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
            <Drawer.HeaderText>우아한형제들</Drawer.HeaderText>
            <Drawer.CloseButton />
          </Drawer.Header>
          {maps.map(({ mapId, mapName, mapImageUrl }) => (
            <Styled.SpaceWrapper key={`map-${mapId}`}>
              <MapListItem
                thumbnail={{ src: mapImageUrl, alt: mapName }}
                title={mapName}
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
