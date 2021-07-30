import { AxiosError } from 'axios';
import { useState } from 'react';
import { useHistory } from 'react-router-dom';
import { ReactComponent as MenuIcon } from 'assets/svg/menu.svg';
import { ReactComponent as MoreIcon } from 'assets/svg/more.svg';
import DateInput from 'components/DateInput/DateInput';
import Drawer from 'components/Drawer/Drawer';
import Header from 'components/Header/Header';
import IconButton from 'components/IconButton/IconButton';
import Layout from 'components/Layout/Layout';
import Panel from 'components/Panel/Panel';
import ReservationListItem from 'components/ReservationListItem/ReservationListItem';
import SpaceListItem from 'components/SpaceListItem/SpaceListItem';
import PATH from 'constants/path';
import { LOCAL_STORAGE_KEY } from 'constants/storage';
import useManagerMaps from 'hooks/useManagerMaps';
import useManagerReservations from 'hooks/useManagerReservations';
import { SpaceReservation } from 'types/common';
import { formatDate } from 'utils/datetime';
import * as Styled from './ManagerMain.styles';

const ManagerMain = (): JSX.Element => {
  const mapId = 1;
  const [date, setDate] = useState(new Date());
  const [open, setOpen] = useState(false);

  const history = useHistory();

  const onRequestError = (error: AxiosError<Error>) => {
    alert(error.response?.data?.message);

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
            reservations.map(
              (
                { spaceId, spaceName, spaceColor, reservations }: SpaceReservation,
                index: number
              ) => (
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
              )
            )}
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
              <SpaceListItem thumbnail={{ src: mapImageUrl, alt: mapName }} title={mapName} />
            </Styled.SpaceWrapper>
          ))}
        </Drawer.Inner>
      </Drawer>
    </>
  );
};

export default ManagerMain;
