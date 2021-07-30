import { useState } from 'react';
import LutherImage from 'assets/images/luther.png';
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
import { Reservation, SpaceReservation } from 'types/common';
import * as Styled from './ManagerMain.styles';
import useManagerReservations from 'hooks/useManagerReservations';
import { formatDate } from 'utils/datetime';

const ManagerMain = (): JSX.Element => {
  const mapId = 1;
  const [date, setDate] = useState(new Date());
  const [open, setOpen] = useState(false);

  const getReservations = useManagerReservations({
    mapId,
    date: formatDate(date),
  });
  const reservations = getReservations.data?.data?.data ?? [];

  const onOpen = () => {
    setOpen(true);
  };

  const onClose = () => {
    setOpen(false);
  };

  const mapName = '우테코 교육장';

  const dummySpaceList = [
    {
      id: 1,
      title: '루터회관 14F',
      thumbnail: {
        src: LutherImage,
        alt: '루터회관 14F 공간',
      },
    },
    {
      id: 2,
      title: '루터회관 14F',
      thumbnail: {
        src: LutherImage,
        alt: '루터회관 14F 공간',
      },
    },
    {
      id: 3,
      title: '루터회관 14F',
      thumbnail: {
        src: LutherImage,
        alt: '루터회관 14F 공간',
      },
    },
    {
      id: 4,
      title: '루터회관 14F',
      thumbnail: {
        src: LutherImage,
        alt: '루터회관 14F 공간',
      },
    },
    {
      id: 5,
      title: '루터회관 14F',
      thumbnail: {
        src: LutherImage,
        alt: '루터회관 14F 공간',
      },
    },
  ];

  return (
    <>
      <Header />
      <Layout>
        <Styled.PageHeader>
          <IconButton text="맵 목록" onClick={onOpen}>
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
                              reservation={reservation as Reservation}
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

      <Drawer open={open} placement="left" maxwidth="450px" onClose={onClose}>
        <Drawer.Inner>
          <Drawer.Header>
            <Drawer.HeaderText>우아한형제들</Drawer.HeaderText>
            <Drawer.CloseButton />
          </Drawer.Header>
          {dummySpaceList.map((space) => (
            <Styled.SpaceWrapper key={`map-${space.id}`}>
              <SpaceListItem thumbnail={space.thumbnail} title={space.title} />
            </Styled.SpaceWrapper>
          ))}
        </Drawer.Inner>
      </Drawer>
    </>
  );
};

export default ManagerMain;
