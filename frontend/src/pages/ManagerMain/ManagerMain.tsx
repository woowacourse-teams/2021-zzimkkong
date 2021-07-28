import { useState } from 'react';
import LutherImage from 'assets/images/luther.png';
import { ReactComponent as LinkIcon } from 'assets/svg/link.svg';
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
import { Reservation } from 'types/common';
import * as Styled from './ManagerMain.styles';

const ManagerMain = (): JSX.Element => {
  const [date, setDate] = useState(new Date());
  const [open, setOpen] = useState(false);

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

  const data = [
    {
      spaceId: 1,
      spaceName: '회의실 1',
      spaceColor: '#ffc757',
      reservations: [
        {
          id: 1,
          name: '썬',
          description: '태양을 피하는 방법',
          startDateTime: '2021-07-23T12:00',
          endDateTime: '2021-07-23T13:00',
        },
        {
          id: 2,
          name: '체프',
          description: '커피를 맛있게 마시는 방법',
          startDateTime: '2021-07-23T13:00',
          endDateTime: '2021-07-23T15:00',
        },
        {
          id: 3,
          name: '유조',
          description: 'Tailwind CSS 가이드',
          startDateTime: '2021-07-23T15:00',
          endDateTime: '2021-07-23T16:00',
        },
      ],
    },
    {
      spaceId: 2,
      spaceName: '회의실 2',
      spaceColor: '#ffc757',
      reservations: [],
    },
    {
      spaceId: 3,
      spaceName: '회의실 3',
      spaceColor: '#ffc757',
      reservations: [],
    },
    {
      spaceId: 4,
      spaceName: '백엔드 강의장',
      spaceColor: '#fda3a7',
      reservations: [],
    },
    {
      spaceId: 5,
      spaceName: '프론트엔드 강의장',
      spaceColor: '#fda3a7',
      reservations: [
        {
          id: 1,
          name: '체프',
          description: '핸드드립 내리는 방법',
          startDateTime: '2021-07-23T15:00',
          endDateTime: '2021-07-23T16:00',
        },
      ],
    },
  ];

  return (
    <>
      <Header />
      <Layout>
        <Styled.PageHeader>
          <IconButton text="맵 목록" onClick={onOpen}>
            <MenuIcon />
          </IconButton>
          <Styled.PageTitle>{mapName}</Styled.PageTitle>
          <IconButton variant="primary" text="공유 링크">
            <LinkIcon />
          </IconButton>
        </Styled.PageHeader>
        <Styled.DateInputWrapper>
          <DateInput date={date} setDate={setDate} />
        </Styled.DateInputWrapper>
        <Styled.SpaceList>
          {data.map(({ spaceId, spaceName, spaceColor, reservations }, index) => (
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
                              <MoreIcon />
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
