import { AxiosError } from 'axios';
import React, { useMemo, useState } from 'react';
import { useMutation } from 'react-query';
import { useHistory, useParams } from 'react-router';
import { postMap, putMap } from 'api/managerMap';
import Button from 'components/Button/Button';
import Header from 'components/Header/Header';
import Layout from 'components/Layout/Layout';
import MESSAGE from 'constants/message';
import PATH, { HREF } from 'constants/path';
import useManagerMap from 'hooks/query/useManagerMap';
import useManagerSpaces from 'hooks/query/useManagerSpaces';
import useInputs from 'hooks/useInputs';
import useListenManagerMainState from 'hooks/useListenManagerMainState';
import { ManagerSpace, MapDrawing, MapElement, SpaceArea } from 'types/common';
import { ErrorResponse } from 'types/response';
import { createMapImageSvg } from 'utils/map';
import * as Styled from './ManagerMapEditor.styles';
import MapEditor from './units/MapEditor';

interface Params {
  mapId?: string;
}

interface Board {
  name: string;
  width: string;
  height: string;
}

const ManagerMapEditor = (): JSX.Element => {
  const history = useHistory();
  const params = useParams<Params>();
  const mapId = params?.mapId;
  const isEdit = !!mapId;

  const [mapElements, setMapElements] = useState<MapElement[]>([]);
  const [{ name, width, height }, onChangeBoard, setBoard] = useInputs<Board>({
    name: '',
    width: '800',
    height: '600',
  });

  const managerSpaces = useManagerSpaces({ mapId: Number(mapId) }, { enabled: isEdit });
  const spaces: ManagerSpace[] = useMemo(() => {
    try {
      return (
        managerSpaces.data?.data.spaces.map((space) => ({
          ...space,
          area: JSON.parse(space.area) as SpaceArea,
        })) ?? []
      );
    } catch (error) {
      return [];
    }
  }, [managerSpaces.data?.data.spaces]);

  useManagerMap(
    { mapId: Number(mapId) },
    {
      enabled: isEdit,
      onSuccess: ({ data }) => {
        const { mapName, mapDrawing } = data;

        try {
          const { mapElements, width, height } = JSON.parse(mapDrawing) as MapDrawing;

          setMapElements(mapElements);
          setBoard({
            name: mapName ?? '',
            width: `${width}`,
            height: `${height}`,
          });
        } catch (error) {
          setMapElements([]);
        }
      },
    }
  );

  const createMap = useMutation(postMap, {
    onSuccess: (response) => {
      if (window.confirm(MESSAGE.MANAGER_MAP.CREATE_SUCCESS_CONFIRM)) {
        const headers = response.headers as { location: string };
        const mapId = Number(headers.location.split('/').pop());

        history.push(HREF.MANAGER_SPACE_EDIT(mapId));

        return;
      }

      history.push(PATH.MANAGER_MAIN);
    },
    onError: (error: AxiosError<ErrorResponse>) => {
      alert(error?.response?.data.message ?? MESSAGE.MANAGER_MAP.UNEXPECTED_MAP_CREATE_ERROR);
    },
  });

  const updateMap = useMutation(putMap, {
    onSuccess: () => {
      alert(MESSAGE.MANAGER_MAP.UPDATE_SUCCESS);
    },
    onError: (error: AxiosError<ErrorResponse>) => {
      alert(error?.response?.data.message ?? MESSAGE.MANAGER_MAP.UNEXPECTED_MAP_UPDATE_ERROR);
    },
  });

  const handleCancel = () => {
    if (!window.confirm(MESSAGE.MANAGER_MAP.CANCEL_CONFIRM)) return;

    history.push(PATH.MANAGER_MAIN);
  };

  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    if (createMap.isLoading || updateMap.isLoading) return;

    const mapDrawing = JSON.stringify({ width, height, mapElements });
    const mapImageSvg = createMapImageSvg({
      mapElements,
      spaces,
      width,
      height,
    });

    if (isEdit) {
      updateMap.mutate({ mapId: Number(mapId), mapName: name, mapDrawing, mapImageSvg });

      return;
    }

    createMap.mutate({ mapName: name, mapDrawing, mapImageSvg });
  };

  useListenManagerMainState({ mapId: Number(mapId) }, { enabled: isEdit });

  return (
    <>
      <Styled.MapCreateGlobalStyle />
      <Header />
      <Layout>
        <Styled.Container>
          <Styled.Form onSubmit={handleSubmit}>
            <Styled.FormHeader>
              <Styled.MapNameInput
                name="name"
                value={name}
                onChange={onChangeBoard}
                placeholder="맵 이름을 입력해주세요"
                required
              />
              <Styled.FormControl>
                <Button type="button" variant="text" onClick={handleCancel}>
                  취소
                </Button>
                <Button variant="primary">{isEdit ? '수정' : '완료'}</Button>
              </Styled.FormControl>
            </Styled.FormHeader>
            <MapEditor
              spaces={spaces}
              mapElementsState={[mapElements, setMapElements]}
              boardState={[{ width, height }, onChangeBoard]}
            />
          </Styled.Form>
        </Styled.Container>
      </Layout>
    </>
  );
};

export default ManagerMapEditor;
