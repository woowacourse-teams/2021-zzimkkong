import { ReactComponent as LinkIcon } from 'assets/svg/link.svg';
import { ReactComponent as MenuIcon } from 'assets/svg/menu.svg';
import { ReactComponent as MoreIcon } from 'assets/svg/more.svg';
import DateInput from 'components/DateInput/DateInput';
import Header from 'components/Header/Header';
import IconButton from 'components/IconButton/IconButton';
import Layout from 'components/Layout/Layout';
import Panel from 'components/Panel/Panel';
import Table from 'components/Table/Table';
import * as Styled from './ManagerMain.styles';

const ManagerMain = (): JSX.Element => {
  const mapName = '우테코 교육장';

  const data = [
    {
      spaceId: 1,
      spaceName: '회의실 1',
      spaceColor: '#FFE3AC',
      reservations: [
        { id: 1, name: '썬', description: '태양을 피하는 방법', time: '12:00 - 13:00' },
        { id: 2, name: '체프', description: '커피를 맛있게 마시는 방법', time: '13:00 - 15:00' },
        { id: 3, name: '유조', description: 'Tailwind CSS 가이드', time: '15:00 - 16:00' },
      ],
    },
    {
      spaceId: 2,
      spaceName: '회의실 2',
      spaceColor: '#FFE3AC',
      reservations: [],
    },
    {
      spaceId: 3,
      spaceName: '회의실 3',
      spaceColor: '#FFE3AC',
      reservations: [],
    },
    {
      spaceId: 4,
      spaceName: '백엔드 강의장',
      spaceColor: '#FED7D9',
      reservations: [],
    },
    {
      spaceId: 5,
      spaceName: '프론트엔드 강의장',
      spaceColor: '#FED7D9',
      reservations: [
        { id: 1, name: '체프', description: '핸드드립 내리는 방법', time: '13:00 - 15:00' },
      ],
    },
  ];

  return (
    <>
      <Header />
      <Layout>
        <Styled.PageHeader>
          <IconButton text="맵 목록">
            <MenuIcon />
          </IconButton>
          <Styled.PageTitle>{mapName}</Styled.PageTitle>
          <IconButton variant="primary" text="공유 링크">
            <LinkIcon />
          </IconButton>
        </Styled.PageHeader>
        <Styled.DateInputWrapper>
          <DateInput value="2021-07-26" />
        </Styled.DateInputWrapper>
        <Styled.SpaceList>
          {data.map(({ spaceId, spaceName, spaceColor, reservations }, index) => (
            <Styled.SpaceListItem key={spaceId}>
              <Panel expandable initialExpanded={!index}>
                <Panel.Header dotColor={spaceColor}>
                  <Panel.Title>{spaceName}</Panel.Title>
                </Panel.Header>
                <Panel.Content>
                  {reservations.length === 0 ? (
                    <Styled.PanelMessage>등록된 예약이 없습니다</Styled.PanelMessage>
                  ) : (
                    <Table>
                      <Table.Head>
                        <Table.Row>
                          <Table.Header wordWrap>이름</Table.Header>
                          <Table.Header wordWrap>주제</Table.Header>
                          <Table.Header wordWrap>시간</Table.Header>
                          <Table.Header wordWrap>관리</Table.Header>
                        </Table.Row>
                      </Table.Head>
                      <Table.Body>
                        {reservations.map(({ id, name, description, time }) => (
                          <Table.Row key={id}>
                            <Table.Cell wordWrap>{name}</Table.Cell>
                            <Table.Cell>{description}</Table.Cell>
                            <Table.Cell>{time}</Table.Cell>
                            <Table.Cell>
                              <MoreIcon />
                            </Table.Cell>
                          </Table.Row>
                        ))}
                      </Table.Body>
                    </Table>
                  )}
                </Panel.Content>
              </Panel>
            </Styled.SpaceListItem>
          ))}
        </Styled.SpaceList>
      </Layout>
    </>
  );
};

export default ManagerMain;
