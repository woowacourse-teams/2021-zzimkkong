import LutherImage from 'assets/images/luther.png';
import Header from 'components/Header/Header';
import Layout from 'components/Layout/Layout';
import SpaceListItem from 'components/SpaceListItem/SpaceListItem';
import * as Styled from './Main.styles';

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

const Main = (): JSX.Element => {
  return (
    <>
      <Header />
      <Layout>
        <Styled.PageHeader>우아한 형제들 공간 목록</Styled.PageHeader>
        <Styled.Container>
          {dummySpaceList.map((space) => (
            <Styled.ItemWrapper>
              <SpaceListItem key={space.id} thumbnail={space.thumbnail} title={space.title} />
            </Styled.ItemWrapper>
          ))}
        </Styled.Container>
      </Layout>
    </>
  );
};

export default Main;
