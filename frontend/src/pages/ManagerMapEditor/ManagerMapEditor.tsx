import { AxiosError } from 'axios';
import React, { createRef, useMemo, useState } from 'react';
import { useMutation } from 'react-query';
import { useHistory, useParams } from 'react-router';
import { postMapV2, putMapV2 } from 'api-v2/managerMap';
import Button from 'components/Button/Button';
import EditorOverlay from 'components/EditorOverlay/EditorOverlay';
import Header from 'components/Header/Header';
import Layout from 'components/Layout/Layout';
import { BOARD } from 'constants/editor';
import MESSAGE from 'constants/message';
import PATH, { HREF } from 'constants/path';
import useManagerMapV2 from 'hooks/query-v2/useManagerMapV2';
import useManagerSpaces from 'hooks/query/useManagerSpaces';
import useInputs from 'hooks/useInputs';
import { Area, ManagerSpace, MapDrawing, MapElement } from 'types/common';
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
    width: `${BOARD.DEFAULT_WIDTH}`,
    height: `${BOARD.DEFAULT_HEIGHT}`,
  });
  const [mapSlackUrl, setMapSlackUrl] = useState('');

  const managerSpaces = useManagerSpaces({ mapId: Number(mapId) }, { enabled: isEdit });
  const spaces: ManagerSpace[] = useMemo(() => {
    try {
      return (
        managerSpaces.data?.data.spaces.map((space) => ({
          ...space,
          area: JSON.parse(space.area) as Area,
        })) ?? []
      );
    } catch (error) {
      return [];
    }
  }, [managerSpaces.data?.data.spaces]);

  useManagerMapV2(
    { mapId: Number(mapId) },
    {
      enabled: isEdit,
      refetchOnWindowFocus: false,
      onSuccess: ({ data }) => {
        const { mapName, mapDrawing, slackUrl } = data;
        setMapSlackUrl(slackUrl);
        try {
          const { mapElements, width, height } = JSON.parse(mapDrawing) as MapDrawing;
          const mapElementsWithRef = mapElements.map((element) => ({
            ...element,
            ref: createRef<SVGPolylineElement | SVGRectElement>(),
          }));

          setMapElements(mapElementsWithRef);
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

  const createMap = useMutation(postMapV2, {
    onSuccess: (response) => {
      const headers = response.headers as { location: string };
      const mapId = Number(headers.location.split('/').pop());

      if (window.confirm(MESSAGE.MANAGER_MAP.CREATE_SUCCESS_CONFIRM)) {
        history.push(HREF.MANAGER_SPACE_EDIT(mapId));

        return;
      }

      history.push(HREF.MANAGER_MAP_DETAIL(mapId));
    },
    onError: (error: AxiosError<ErrorResponse>) => {
      alert(error?.response?.data.message ?? MESSAGE.MANAGER_MAP.UNEXPECTED_MAP_CREATE_ERROR);
    },
  });

  const updateMap = useMutation(putMapV2, {
    onSuccess: () => {
      alert(MESSAGE.MANAGER_MAP.UPDATE_SUCCESS);
    },
    onError: (error: AxiosError<ErrorResponse>) => {
      alert(error?.response?.data.message ?? MESSAGE.MANAGER_MAP.UNEXPECTED_MAP_UPDATE_ERROR);
    },
  });

  const handleCancel = () => {
    if (!window.confirm(MESSAGE.MANAGER_MAP.CANCEL_CONFIRM)) return;

    history.push(PATH.MANAGER_MAP_LIST);
  };

  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    if (createMap.isLoading || updateMap.isLoading) return;

    const mapDrawing = JSON.stringify({
      width,
      height,
      mapElements: mapElements.map(({ ref, ...props }) => props),
    });

    const thumbnail = createMapImageSvg({
      mapElements,
      spaces,
      width,
      height,
    });

    if (isEdit) {
      updateMap.mutate({
        mapId: Number(mapId),
        mapName: name,
        mapDrawing,
        thumbnail,
        slackUrl: mapSlackUrl,
      });

      return;
    }

    createMap.mutate({ mapName: name, mapDrawing, thumbnail, slackUrl: mapSlackUrl });
  };

  return (
    <>
      <Styled.MapCreateGlobalStyle />
      <Header />
      <Layout>
        <Styled.Desktop>
          <Styled.Container>
            <Styled.Form onSubmit={handleSubmit}>
              <Styled.FormHeader>
                <Styled.MapNameInput
                  name="name"
                  value={name}
                  onChange={onChangeBoard}
                  placeholder="맵 이름을 입력해주세요"
                  maxLength={20}
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
        </Styled.Desktop>
        <Styled.Mobile>
          <EditorOverlay />
        </Styled.Mobile>
      </Layout>
    </>
  );
};

export default ManagerMapEditor;
