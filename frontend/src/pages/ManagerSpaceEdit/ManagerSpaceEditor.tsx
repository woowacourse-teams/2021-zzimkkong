import { useParams } from 'react-router-dom';
import Header from 'components/Header/Header';
import Layout from 'components/Layout/Layout';
import useListenManagerMainState from 'hooks/useListenManagerMainState';
import useManagerMap from 'hooks/useManagerMap';
import * as Styled from './ManagerSpaceEditor.styles';
import { SpaceEditorMode as Mode } from './constants';
import useSpaceEditorMode from './hooks/useSpaceEditorMode';
import EditorHeader from './units/EditorHeader';
import ShapeSelectToolbar from './units/ShapeSelectToolbar';

const ManagerSpaceEditor = (): JSX.Element => {
  const { mapId } = useParams<{ mapId: string }>();
  useListenManagerMainState({ mapId: Number(mapId) });
  const map = useManagerMap({ mapId: Number(mapId) });
  const mapName = map.data?.data.mapName ?? '';

  const [mode, setMode] = useSpaceEditorMode();

  const isDrawing = mode === Mode.Rect;

  return (
    <>
      <Header />
      <Layout>
        <Styled.Page>
          <EditorHeader mapName={mapName} />

          <Styled.EditorMain>
            <Styled.EditorContainer>
              {isDrawing && <ShapeSelectToolbar mode={mode} setMode={setMode} />}
            </Styled.EditorContainer>

            <Styled.FormContainer disabled={mode !== Mode.Form}></Styled.FormContainer>
          </Styled.EditorMain>
        </Styled.Page>
      </Layout>
    </>
  );
};

export default ManagerSpaceEditor;
