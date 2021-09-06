import { useRef } from 'react';
import { useHistory, useParams } from 'react-router';
import Button from 'components/Button/Button';
import Header from 'components/Header/Header';
import Layout from 'components/Layout/Layout';
import useListenManagerMainState from 'hooks/useListenManagerMainState';
import * as Styled from './MapCreate.styles';
import MapCreateEditor from './MapCreateEditor';

interface Params {
  mapId?: string;
}

const MapCreate = (): JSX.Element => {
  const history = useHistory();
  const params = useParams<Params>();
  const mapId = params?.mapId;
  const isEdit = !!mapId;

  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
  };

  useListenManagerMainState({ mapId: Number(mapId) });

  return (
    <>
      <Styled.MapCreateGlobalStyle />
      <Header />
      <Layout>
        <Styled.Container>
          <Styled.Form onSubmit={handleSubmit}>
            <Styled.MapNameInput placeholder="맵 이름을 입력해주세요" required />
            <Styled.FormControl>
              <Button type="button" variant="text">
                취소
              </Button>
              <Button variant="primary">{isEdit ? '수정' : '완료'}</Button>
            </Styled.FormControl>
          </Styled.Form>
          <MapCreateEditor />
        </Styled.Container>
      </Layout>
    </>
  );
};

export default MapCreate;
