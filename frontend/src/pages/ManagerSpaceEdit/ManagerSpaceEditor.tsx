import { AxiosError } from 'axios';
import { FormEventHandler, useMemo, useState } from 'react';
import { useMutation } from 'react-query';
import { useParams } from 'react-router-dom';
import { putManagerSpace, PutManagerSpaceParams } from 'api/managerSpace';
import { ReactComponent as PlusSmallIcon } from 'assets/svg/plus-small.svg';
import Button from 'components/Button/Button';
import Header from 'components/Header/Header';
import Layout from 'components/Layout/Layout';
import { BOARD } from 'constants/editor';
import MESSAGE from 'constants/message';
import useListenManagerMainState from 'hooks/useListenManagerMainState';
import useManagerMap from 'hooks/useManagerMap';
import useManagerSpaces from 'hooks/useManagerSpaces';
import { ManagerSpace, MapDrawing, SpaceArea } from 'types/common';
import { ErrorResponse } from 'types/response';
import * as Styled from './ManagerSpaceEditor.styles';
import { SpaceEditorMode as Mode } from './constants';
import useBoardStatus from './hooks/useBoardStatus';
import SpaceFormProvider from './providers/SpaceFormProvider';
import Editor from './units/Editor';
import EditorHeader from './units/EditorHeader';
import Form from './units/Form';
import ShapeSelectToolbar from './units/ShapeSelectToolbar';
import SpaceSelect from './units/SpaceSelect';

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

  const [mode, setMode] = useState<Mode>(Mode.Default);
  const [board, setBoard] = useBoardStatus({ width, height });
  const [selectedSpaceId, setSelectedSpaceId] = useState<number | null>(null);

  const isDrawing = mode === Mode.Rect;

  const updateSpace = useMutation(putManagerSpace, {
    onSuccess: () => {
      managerSpaces.refetch();
      alert(MESSAGE.MANAGER_SPACE.SPACE_SETTING_UPDATED);
    },
    onError: (error: AxiosError<ErrorResponse>) => {
      alert(error.response?.data.message ?? MESSAGE.MANAGER_SPACE.EDIT_UNEXPECTED_ERROR);
    },
  });

  const handleUpdateSpace = (data: Omit<PutManagerSpaceParams, 'mapId'>) =>
    updateSpace.mutate({ mapId: Number(mapId), ...data });

  const handleAddSpace = () => {
    setMode(Mode.Rect);
    setSelectedSpaceId(null);
  };

  return (
    <>
      <Header />
      <Layout>
        <Styled.Page>
          <EditorHeader mapName={mapName} />

          <Styled.EditorMain>
            <SpaceFormProvider>
              <Styled.EditorContainer>
                {isDrawing && <ShapeSelectToolbar mode={mode} setMode={setMode} />}

                <Editor
                  mode={mode}
                  boardState={[board, setBoard]}
                  selectedSpaceIdState={[selectedSpaceId, setSelectedSpaceId]}
                  mapElements={mapElements}
                  spaces={spaces}
                />
              </Styled.EditorContainer>

              <Styled.FormContainer disabled={isDrawing}>
                <SpaceSelect
                  spaces={spaces}
                  selectedSpaceIdState={[selectedSpaceId, setSelectedSpaceId]}
                  disabled={isDrawing}
                >
                  <Styled.AddButtonWrapper>
                    <Button variant="primary" shape="round" onClick={handleAddSpace}>
                      <PlusSmallIcon /> 공간 추가
                    </Button>
                  </Styled.AddButtonWrapper>
                </SpaceSelect>

                {selectedSpaceId !== null || isDrawing ? (
                  <Form
                    mapData={{ width: board.width, height: board.height, mapElements }}
                    spaces={spaces}
                    selectedSpaceId={selectedSpaceId}
                    disabled={isDrawing}
                    onUpdateSpace={handleUpdateSpace}
                  />
                ) : (
                  <Styled.NoSpaceMessage>공간을 선택해주세요</Styled.NoSpaceMessage>
                )}
              </Styled.FormContainer>
            </SpaceFormProvider>
          </Styled.EditorMain>
        </Styled.Page>
      </Layout>
    </>
  );
};

export default ManagerSpaceEditor;
