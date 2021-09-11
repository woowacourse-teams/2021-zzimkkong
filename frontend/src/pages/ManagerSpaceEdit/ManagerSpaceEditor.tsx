import { useMemo } from 'react';
import { useParams } from 'react-router-dom';
import Header from 'components/Header/Header';
import Layout from 'components/Layout/Layout';
import { BOARD } from 'constants/editor';
import MESSAGE from 'constants/message';
import useListenManagerMainState from 'hooks/useListenManagerMainState';
import useManagerMap from 'hooks/useManagerMap';
import useManagerSpaces from 'hooks/useManagerSpaces';
import { ManagerSpace, MapDrawing, SpaceArea } from 'types/common';
import * as Styled from './ManagerSpaceEditor.styles';
import { SpaceEditorMode as Mode } from './constants';
import useBoardStatus from './hooks/useBoardStatus';
import useSpaceEditorMode from './hooks/useSpaceEditorMode';
import Editor from './units/Editor';
import EditorHeader from './units/EditorHeader';
import ShapeSelectToolbar from './units/ShapeSelectToolbar';

const ManagerSpaceEditor = (): JSX.Element => {
  const { mapId } = useParams<{ mapId: string }>();
  useListenManagerMainState({ mapId: Number(mapId) });
  const map = useManagerMap({ mapId: Number(mapId) });
  const mapName = map.data?.data.mapName ?? '';
  const { width, height, mapElements } = useMemo(() => {
    try {
      return JSON.parse(map.data?.data.mapDrawing ?? '{}') as MapDrawing;
    } catch (error) {
      alert(MESSAGE.MANAGER_SPACE.GET_UNEXPECTED_ERROR);

      return { width: BOARD.DEFAULT_WIDTH, height: BOARD.DEFAULT_HEIGHT, mapElements: [] };
    }
  }, [map.data?.data.mapDrawing]);

  const managerSpaces = useManagerSpaces({ mapId: Number(mapId) });
  const spaces: ManagerSpace[] = useMemo(
    () =>
      managerSpaces.data?.data.spaces.map((space) => ({
        ...space,
        area: JSON.parse(space.area) as SpaceArea,
      })) ?? [],
    [managerSpaces.data?.data.spaces]
  );

  const [mode, setMode] = useSpaceEditorMode();
  const [boardStatus, setBoardStatus] = useBoardStatus({ width, height });

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

              <Editor
                mode={mode}
                boardState={[boardStatus, setBoardStatus]}
                mapElements={mapElements}
                spaces={spaces}
              />
            </Styled.EditorContainer>

            <Styled.FormContainer disabled={mode !== Mode.Form}></Styled.FormContainer>
          </Styled.EditorMain>
        </Styled.Page>
      </Layout>
    </>
  );
};

export default ManagerSpaceEditor;
