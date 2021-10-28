import { AxiosError } from 'axios';
import { useEffect, useMemo, useState } from 'react';
import { useMutation } from 'react-query';
import { useParams } from 'react-router-dom';
import {
  deleteManagerSpace,
  DeleteManagerSpaceParams,
  postManagerSpace,
  PostManagerSpaceParams,
  putManagerSpace,
  PutManagerSpaceParams,
} from 'api/managerSpace';
import Header from 'components/Header/Header';
import Layout from 'components/Layout/Layout';
import MobileRedirect from 'components/MobileRedirect/MobileRedirect';
import { BOARD } from 'constants/editor';
import MESSAGE from 'constants/message';
import useBoardStatus from 'hooks/board/useBoardStatus';
import useManagerMap from 'hooks/query/useManagerMap';
import useManagerSpaces from 'hooks/query/useManagerSpaces';
import useListenManagerMainState from 'hooks/useListenManagerMainState';
import { Area, ManagerSpace, MapDrawing } from 'types/common';
import { SpaceEditorMode as Mode } from 'types/editor';
import { ErrorResponse } from 'types/response';
import * as Styled from './ManagerSpaceEditor.styles';
import { drawingModes } from './data';
import SpaceFormProvider from './providers/SpaceFormProvider';
import Editor from './units/Editor';
import EditorHeader from './units/EditorHeader';
import Form from './units/Form';
import ShapeSelectToolbar from './units/ShapeSelectToolbar';
import SpaceAddButton from './units/SpaceAddButton';
import SpaceSelect from './units/SpaceSelect';

interface CreateResponseHeaders {
  location: string;
}

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
  const spaces: ManagerSpace[] = useMemo(() => {
    try {
      return (
        managerSpaces.data?.data.spaces.map((space) => ({
          ...space,
          area: JSON.parse(space.area) as Area,
        })) ?? []
      );
    } catch (error) {
      alert(MESSAGE.MANAGER_SPACE.GET_UNEXPECTED_ERROR);

      return [];
    }
  }, [managerSpaces.data?.data.spaces]);

  const [mode, setMode] = useState<Mode>(Mode.Default);
  const [board, setBoard] = useBoardStatus({ width, height });
  const [selectedSpaceId, setSelectedSpaceId] = useState<number | null>(null);

  const isDrawingMode = drawingModes.includes(mode);

  const createSpace = useMutation(postManagerSpace, {
    onSuccess: async (response) => {
      const { location } = response.headers as CreateResponseHeaders;
      const newSpaceId = Number(location.split('/').pop() ?? '');

      await managerSpaces.refetch();
      setSelectedSpaceId(newSpaceId);
      alert(MESSAGE.MANAGER_SPACE.SPACE_CREATED);
    },
    onError: (error: AxiosError<ErrorResponse>) => {
      alert(error.response?.data.message ?? MESSAGE.MANAGER_SPACE.ADD_UNEXPECTED_ERROR);
    },
  });

  const updateSpace = useMutation(putManagerSpace, {
    onSuccess: () => {
      managerSpaces.refetch();
      alert(MESSAGE.MANAGER_SPACE.SPACE_SETTING_UPDATED);
    },
    onError: (error: AxiosError<ErrorResponse>) => {
      alert(error.response?.data.message ?? MESSAGE.MANAGER_SPACE.EDIT_UNEXPECTED_ERROR);
    },
  });

  const deleteSpace = useMutation(deleteManagerSpace, {
    onSuccess: () => {
      setSelectedSpaceId(null);
      setMode(Mode.Default);

      managerSpaces.refetch();
      alert(MESSAGE.MANAGER_SPACE.SPACE_DELETED);
    },
    onError: (error: AxiosError<ErrorResponse>) => {
      alert(error.response?.data.message ?? MESSAGE.MANAGER_SPACE.DELETE_UNEXPECTED_ERROR);
    },
  });

  const handleCreateSpace = (data: Omit<PostManagerSpaceParams, 'mapId'>) =>
    createSpace.mutate({ mapId: Number(mapId), ...data });

  const handleUpdateSpace = (data: Omit<PutManagerSpaceParams, 'mapId'>) =>
    updateSpace.mutate({ mapId: Number(mapId), ...data });

  const handleDeleteSpace = (data: Omit<DeleteManagerSpaceParams, 'mapId'>) =>
    deleteSpace.mutate({ mapId: Number(mapId), ...data });

  const handleAddSpace = () => {
    setMode(Mode.Rect);
    setSelectedSpaceId(null);
  };

  useEffect(() => {
    if (selectedSpaceId === null) return;

    setMode(Mode.Form);
  }, [selectedSpaceId]);

  return (
    <>
      <Header />

      <Layout>
        <Styled.Desktop>
          <Styled.Page>
            <EditorHeader mapName={mapName} />

            <Styled.EditorMain>
              <SpaceFormProvider>
                <Styled.EditorContainer>
                  {isDrawingMode && <ShapeSelectToolbar mode={mode} setMode={setMode} />}

                  <Editor
                    modeState={[mode, setMode]}
                    boardState={[board, setBoard]}
                    selectedSpaceIdState={[selectedSpaceId, setSelectedSpaceId]}
                    mapElements={mapElements}
                    spaces={spaces}
                  />
                </Styled.EditorContainer>

                <Styled.FormContainer disabled={isDrawingMode}>
                  <SpaceSelect
                    spaces={spaces}
                    selectedSpaceIdState={[selectedSpaceId, setSelectedSpaceId]}
                    disabled={isDrawingMode}
                  >
                    <Styled.AddButtonWrapper>
                      <SpaceAddButton onClick={handleAddSpace} />
                    </Styled.AddButtonWrapper>
                  </SpaceSelect>

                  {mode === Mode.Form || isDrawingMode ? (
                    <Form
                      modeState={[mode, setMode]}
                      mapData={{ width: board.width, height: board.height, mapElements }}
                      spaces={spaces}
                      selectedSpaceId={selectedSpaceId}
                      disabled={isDrawingMode}
                      onCreateSpace={handleCreateSpace}
                      onUpdateSpace={handleUpdateSpace}
                      onDeleteSpace={handleDeleteSpace}
                    />
                  ) : (
                    <Styled.NoSpaceMessage>공간을 선택해주세요</Styled.NoSpaceMessage>
                  )}
                </Styled.FormContainer>
              </SpaceFormProvider>
            </Styled.EditorMain>
          </Styled.Page>
        </Styled.Desktop>
        <Styled.Mobile>
          <MobileRedirect />
        </Styled.Mobile>
      </Layout>
    </>
  );
};

export default ManagerSpaceEditor;
