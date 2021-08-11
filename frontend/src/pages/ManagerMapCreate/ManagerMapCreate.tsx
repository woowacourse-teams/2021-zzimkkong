import { AxiosError } from 'axios';
import {
  FocusEventHandler,
  FormEventHandler,
  MouseEvent,
  MouseEventHandler,
  useCallback,
  useEffect,
  useMemo,
  useRef,
  useState,
  WheelEventHandler,
} from 'react';
import { useMutation } from 'react-query';
import { useHistory, useParams } from 'react-router-dom';
import { postMap, putMap } from 'api/managerMap';
import { ReactComponent as EraserIcon } from 'assets/svg/eraser.svg';
import { ReactComponent as ItemsIcon } from 'assets/svg/items.svg';
import { ReactComponent as LineIcon } from 'assets/svg/line.svg';
import { ReactComponent as MoveIcon } from 'assets/svg/move.svg';
import { ReactComponent as SelectIcon } from 'assets/svg/select.svg';
import { ReactComponent as SquareIcon } from 'assets/svg/square.svg';
import Button from 'components/Button/Button';
import ColorPicker from 'components/ColorPicker/ColorPicker';
import ColorPickerIcon from 'components/ColorPicker/ColorPickerIcon';
import Header from 'components/Header/Header';
import Layout from 'components/Layout/Layout';
import { BOARD } from 'constants/editor';
import MESSAGE from 'constants/message';
import PALETTE from 'constants/palette';
import PATH, { HREF } from 'constants/path';
import useInput from 'hooks/useInput';
import useManagerMap from 'hooks/useManagerMap';
import useManagerSpaces from 'hooks/useManagerSpaces';
import {
  Color,
  Coordinate,
  DrawingStatus,
  EditorBoard,
  GripPoint,
  ManagerSpace,
  MapDrawing,
  MapElement,
  SpaceArea,
} from 'types/common';
import { Mode } from 'types/editor';
import { ErrorResponse } from 'types/response';
import * as Styled from './ManagerMapCreate.styles';

const GRID_SIZE = 10;
const SCALE_DELTA = 0.001;
const MIN_SCALE = 0.5;
const MAX_SCALE = 3.0;
const LINE_WIDTH = 3;
const KEY_DELETE = 'Delete';
const KEY_SPACE = ' ';

interface Params {
  mapId?: string;
}

const ManagerMapCreate = (): JSX.Element => {
  const editorRef = useRef<HTMLDivElement | null>(null);

  const history = useHistory();
  const params = useParams<Params>();
  const mapId = params?.mapId;
  const isEdit = !!mapId;

  const [mapName, onChangeMapName, setMapName] = useInput('');

  const [mode, setMode] = useState(Mode.Select);
  const [dragOffsetX, setDragOffsetX] = useState(0);
  const [dragOffsetY, setDragOffsetY] = useState(0);
  const [isDragging, setDragging] = useState(false);
  const [isPressSpacebar, setPressSpacebar] = useState(false);
  const isDraggable = mode === Mode.Move || isPressSpacebar;

  const [color, setColor] = useState<Color>(PALETTE.BLACK[400]);
  const [colorPickerOpen, setColorPickerOpen] = useState(false);

  const [coordinate, setCoordinate] = useState<Coordinate>({ x: 0, y: 0 });

  const [stickyPointerView, setStickyPointerView] = useState(false);

  const stickyCoordinate: Coordinate = {
    x: Math.round(coordinate.x / GRID_SIZE) * GRID_SIZE,
    y: Math.round(coordinate.y / GRID_SIZE) * GRID_SIZE,
  };

  const [drawingStatus, setDrawingStatus] = useState<DrawingStatus>({});
  const [mapElements, setMapElements] = useState<MapElement[]>([]);

  const [gripPoints, setGripPoints] = useState<GripPoint[]>([]);
  const [selectedMapElementId, setSelectedMapElementId] = useState<MapElement['id'] | null>(null);
  const [erasingMapElementIds, setErasingMapElementIds] = useState<MapElement['id'][]>([]);
  const [isErasing, setErasing] = useState(false);

  const nextMapElementId = Math.max(...mapElements.map(({ id }) => id), 1) + 1;
  const nextGripPointId = Math.max(...gripPoints.map(({ id }) => id), 1) + 1;

  const [widthValue, onChangeWidthValue, setWidthValue] = useInput('800');
  const [heightValue, onChangeHeightValue, setHeightValue] = useInput('600');

  const width = Number(widthValue);
  const height = Number(heightValue);

  const [board, setBoard] = useState<EditorBoard>({
    width,
    height,
    x: 0,
    y: 0,
    scale: 1,
  });

  const managerSpaces = useManagerSpaces({ mapId: Number(mapId) }, { enabled: isEdit });
  const spaces: ManagerSpace[] = useMemo(
    () =>
      managerSpaces.data?.data.spaces.map((space) => ({
        ...space,
        area: JSON.parse(space.area) as SpaceArea,
      })) ?? [],
    [managerSpaces.data?.data.spaces]
  );

  const managerMap = useManagerMap(
    { mapId: Number(mapId) },
    {
      enabled: isEdit,
      onSuccess: ({ data }) => {
        const { mapName, mapDrawing } = data;

        setMapName(mapName ?? '');

        try {
          const { mapElements, width, height } = JSON.parse(mapDrawing) as MapDrawing;

          setMapElements(mapElements);
          setWidthValue(`${width}`);
          setHeightValue(`${height}`);
        } catch (error) {
          console.error(error);
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
      console.error(error);
    },
  });

  const updateMap = useMutation(putMap, {
    onSuccess: () => {
      alert(MESSAGE.MANAGER_MAP.UPDATE_SUCCESS);
    },
    onError: (error: AxiosError<ErrorResponse>) => {
      console.error(error);
    },
  });

  const getSVGCoordinate = (event: MouseEvent<SVGElement>) => {
    const svg = (event.nativeEvent.target as SVGElement)?.ownerSVGElement;
    if (!svg) return { svg: null, x: -1, y: -1 };

    let point = svg.createSVGPoint();

    point.x = event.nativeEvent.clientX;
    point.y = event.nativeEvent.clientY;
    point = point.matrixTransform(svg.getScreenCTM()?.inverse());

    const x = (point.x - board.x) * (1 / board.scale);
    const y = (point.y - board.y) * (1 / board.scale);

    return { svg, x, y };
  };

  const selectMode = (mode: Mode) => {
    setDrawingStatus({});
    setCoordinate({ x: 0, y: 0 });
    setMode(mode);
  };

  const unselectMapElement = () => {
    setSelectedMapElementId(null);
    setGripPoints([]);
  };

  const handleMouseMove: MouseEventHandler<SVGElement> = (event) => {
    const { x, y } = getSVGCoordinate(event);
    setCoordinate({ x, y });
  };

  const handleWheel: WheelEventHandler<SVGElement> = (event) => {
    const { offsetX, offsetY, deltaY } = event.nativeEvent;

    setBoard((prevState) => {
      const { scale, x, y, width, height } = prevState;

      const nextScale = scale - deltaY * SCALE_DELTA;

      if (nextScale <= MIN_SCALE || nextScale >= MAX_SCALE) {
        return {
          ...prevState,
          scale: prevState.scale,
        };
      }

      const cursorX = (offsetX - x) / (width * scale);
      const cursorY = (offsetY - y) / (height * scale);

      const widthDiff = Math.abs(width * nextScale - width * scale) * cursorX;
      const heightDiff = Math.abs(height * nextScale - height * scale) * cursorY;

      const nextX = nextScale > scale ? x - widthDiff : x + widthDiff;
      const nextY = nextScale > scale ? y - heightDiff : y + heightDiff;

      return {
        ...prevState,
        x: nextX,
        y: nextY,
        scale: nextScale,
      };
    });
  };

  const handleDragStart: MouseEventHandler<SVGElement> = (event) => {
    if (!isDraggable) return;

    setDragOffsetX(event.nativeEvent.offsetX - board.x);
    setDragOffsetY(event.nativeEvent.offsetY - board.y);

    setDragging(true);
  };

  const handleDrag: MouseEventHandler<SVGElement> = (event) => {
    if (!isDraggable || !isDragging) return;

    const { offsetX, offsetY } = event.nativeEvent;

    setBoard((prevState) => ({
      ...prevState,
      x: offsetX - dragOffsetX,
      y: offsetY - dragOffsetY,
    }));
  };

  const handleDragEnd = () => {
    if (!isDraggable) return;

    setDragOffsetX(0);
    setDragOffsetY(0);

    setDragging(false);
  };

  const handleMouseOut = () => {
    setDragging(false);
  };

  const handleSelectMapElement = (event: MouseEvent<SVGPolylineElement>, id: MapElement['id']) => {
    if (mode !== Mode.Select) return;

    const target = event.target as SVGPolylineElement;
    const points = Object.values<Coordinate>(target?.points).map(({ x, y }) => ({ x, y }));

    const newGripPoints = points.map(
      (point, index): GripPoint => ({
        id: nextGripPointId + index,
        mapElementId: id,
        x: point.x,
        y: point.y,
      })
    );

    setSelectedMapElementId(id);
    setGripPoints([...newGripPoints]);
  };

  const handleClickBoard: MouseEventHandler<SVGGElement> = (event) => {
    unselectMapElement();
  };

  const drawStart = () => {
    if (drawingStatus.start) {
      const startPoint = `${drawingStatus.start.x},${drawingStatus.start.y}`;
      const endPoint = `${stickyCoordinate.x},${stickyCoordinate.y}`;

      setMapElements((prevState) => [
        ...prevState,
        {
          id: nextMapElementId,
          type: 'polyline',
          stroke: color,
          points: [startPoint, endPoint],
        },
      ]);

      return;
    }

    if (isDragging) return;

    setDrawingStatus((prevState) => ({
      ...prevState,
      start: stickyCoordinate,
    }));
  };

  const drawEnd = () => {
    if (!drawingStatus || !drawingStatus.start) return;

    const startPoint = `${drawingStatus.start.x},${drawingStatus.start.y}`;
    const endPoint = `${stickyCoordinate.x},${stickyCoordinate.y}`;

    setDrawingStatus({});

    if (startPoint === endPoint || isDragging) return;

    setMapElements((prevState) => [
      ...prevState,
      {
        id: nextMapElementId,
        type: 'polyline',
        stroke: color,
        points: [startPoint, endPoint],
      },
    ]);
  };

  const eraseStart = () => {
    if (erasingMapElementIds.length > 0) {
      eraseEnd();

      return;
    }
    setErasing(true);
    setErasingMapElementIds([]);
  };

  const eraseEnd = () => {
    setErasing(false);
    setMapElements((prevMapElements) =>
      prevMapElements.filter(({ id }) => !erasingMapElementIds.includes(id))
    );
    setErasingMapElementIds([]);
  };

  const handleSelectErasingElement = (id: MapElement['id']) => {
    if (mode !== Mode.Eraser || !isErasing) return;

    setErasingMapElementIds((prevIds) => [...prevIds, id]);
  };

  const handleMouseDown = () => {
    if (isDraggable) return;

    if (mode === Mode.Line) drawStart();
    if (mode === Mode.Eraser) eraseStart();
  };

  const handleMouseUp = () => {
    if (isDraggable) return;

    if (mode === Mode.Line) drawEnd();
    if (mode === Mode.Eraser) eraseEnd();
  };

  const deleteMapElement = useCallback(() => {
    if (!selectedMapElementId) return;

    setMapElements((prevMapElements) =>
      prevMapElements.filter(({ id }) => id !== selectedMapElementId)
    );
    unselectMapElement();
  }, [selectedMapElementId]);

  const handleKeyDown = useCallback(
    (event: KeyboardEvent) => {
      if ((event.target as HTMLElement).tagName === 'INPUT') return;

      if (event.key === KEY_DELETE) {
        deleteMapElement();
      }
      if (event.key === KEY_SPACE) {
        setPressSpacebar(true);
      }
    },
    [deleteMapElement]
  );

  const handleKeyUp = useCallback((event: KeyboardEvent) => {
    if (event.key === KEY_SPACE) {
      setPressSpacebar(false);
    }
  }, []);

  const handleClickCancel = () => {
    if (!window.confirm(MESSAGE.MANAGER_MAP.CANCEL_CONFIRM)) return;

    history.push(PATH.MANAGER_MAIN);
  };

  const handleSubmit: FormEventHandler<HTMLFormElement> = (event) => {
    event.preventDefault();

    const mapDrawing = JSON.stringify({ width, height, mapElements });

    const mapImageSvg = `
      <svg
        xmlns='http://www.w3.org/2000/svg'
        version='1.1'
        width='${width}px'
        height='${height}px'
        viewBox='0 0 ${width} ${height}'
      >
        ${spaces
          ?.map(
            ({ color, area }) => `
              <g>
                <rect
                  x='${area.x}'
                  y='${area.y}'
                  width='${area.width}'
                  height='${area.height}'
                  fill='${color}'
                  opacity='0.3'
                />
              </g>`
          )
          .join('')}
        ${mapElements
          .map(
            ({ points, stroke }) => `
              <polyline
                points='${points.join(' ')}'
                stroke='${stroke}'
                strokeWidth='${LINE_WIDTH}'
                strokeLinecap='round'
              />
            `
          )
          .join('')}
      </svg>
    `
      .replace(/(\r\n\t|\n|\r\t|\s{1,})/gm, ' ')
      .replace(/\s{2,}/g, ' ');

    if (isEdit) {
      updateMap.mutate({ mapId: Number(mapId), mapName, mapDrawing, mapImageSvg });
      return;
    }

    createMap.mutate({ mapName, mapDrawing, mapImageSvg });
  };

  const handleWidthSize: FocusEventHandler<HTMLInputElement> = (event) => {
    if (width > BOARD.MAX_WIDTH) {
      event.target.value = String(BOARD.MAX_WIDTH);
      onChangeWidthValue(event);
    }

    if (width < BOARD.MIN_WIDTH) {
      event.target.value = String(BOARD.MIN_WIDTH);
      onChangeWidthValue(event);
    }
  };

  const handleHeightSize: FocusEventHandler<HTMLInputElement> = (event) => {
    if (height > BOARD.MAX_HEIGHT) {
      event.target.value = String(BOARD.MAX_HEIGHT);
      onChangeHeightValue(event);
    }

    if (height < BOARD.MIN_HEIGHT) {
      event.target.value = String(BOARD.MIN_HEIGHT);
      onChangeHeightValue(event);
    }
  };

  useEffect(() => {
    const editorWidth = editorRef.current ? editorRef.current.offsetWidth : 0;
    const editorHeight = editorRef.current ? editorRef.current.offsetHeight : 0;

    setBoard((prevState) => ({
      ...prevState,
      x: (editorWidth - width) / 2,
      y: (editorHeight - height) / 2,
    }));
  }, [width, height]);

  useEffect(() => {
    document.addEventListener('keydown', handleKeyDown);
    document.addEventListener('keyup', handleKeyUp);

    return () => {
      document.removeEventListener('keydown', handleKeyDown);
      document.removeEventListener('keyup', handleKeyUp);
    };
  }, [handleKeyDown, handleKeyUp]);

  return (
    <>
      <Styled.PageGlobalStyle />
      <Header />
      <Layout>
        <Styled.Container>
          <Styled.EditorHeader>
            <Styled.Form onSubmit={handleSubmit}>
              <Styled.HeaderContent>
                <Styled.MapNameContainer>
                  <Styled.MapNameInput
                    placeholder="맵 이름을 입력해주세요"
                    value={mapName}
                    onChange={onChangeMapName}
                    required
                  />
                </Styled.MapNameContainer>
                <Styled.TempSaveContainer>
                  <Styled.TempSaveMessage>1분 전에 임시 저장되었습니다.</Styled.TempSaveMessage>
                  <Styled.TempSaveButton type="button" variant="primary-text">
                    임시 저장
                  </Styled.TempSaveButton>
                </Styled.TempSaveContainer>
              </Styled.HeaderContent>
              <Styled.HeaderContent>
                <Styled.ButtonContainer>
                  <Button type="button" variant="text" onClick={handleClickCancel}>
                    취소
                  </Button>
                  <Button variant="primary">{isEdit ? '수정' : '완료'}</Button>
                </Styled.ButtonContainer>
              </Styled.HeaderContent>
            </Styled.Form>
          </Styled.EditorHeader>
          <Styled.EditorContent>
            <Styled.Toolbar>
              <Styled.ToolbarButton
                text="선택"
                selected={mode === Mode.Select}
                onClick={() => selectMode(Mode.Select)}
              >
                <SelectIcon />
              </Styled.ToolbarButton>
              <Styled.ToolbarButton
                text="이동"
                selected={mode === Mode.Move}
                onClick={() => selectMode(Mode.Move)}
              >
                <MoveIcon />
              </Styled.ToolbarButton>
              <Styled.ToolbarButton
                text="선"
                selected={mode === Mode.Line}
                onClick={() => selectMode(Mode.Line)}
              >
                <LineIcon />
              </Styled.ToolbarButton>
              <Styled.ToolbarButton
                text="사각형"
                selected={mode === Mode.Square}
                onClick={() => selectMode(Mode.Square)}
              >
                <SquareIcon />
              </Styled.ToolbarButton>
              <Styled.ToolbarButton
                text="지우개"
                selected={mode === Mode.Eraser}
                onClick={() => selectMode(Mode.Eraser)}
              >
                <EraserIcon />
              </Styled.ToolbarButton>
              <Styled.ToolbarButton
                text="장식"
                selected={mode === Mode.Decoration}
                onClick={() => selectMode(Mode.Decoration)}
              >
                <ItemsIcon />
              </Styled.ToolbarButton>
              <Styled.ToolbarButton
                text="색상선택"
                onClick={() => setColorPickerOpen(!colorPickerOpen)}
              >
                <ColorPickerIcon color={color} />
              </Styled.ToolbarButton>
              <Styled.ColorPickerWrapper>
                <ColorPicker open={colorPickerOpen} color={color} setColor={setColor} />
              </Styled.ColorPickerWrapper>
            </Styled.Toolbar>
            <Styled.Editor ref={editorRef}>
              <Styled.BoardContainer
                xmlns="http://www.w3.org/2000/svg"
                version="1.1"
                width="100%"
                height="100%"
                isDragging={isDragging}
                isDraggable={isDraggable}
                onWheel={handleWheel}
                onMouseDown={handleDragStart}
                onMouseUp={handleDragEnd}
                onMouseMove={handleDrag}
                onMouseOut={handleMouseOut}
              >
                <rect width="100%" height="100%" fill={PALETTE.GRAY[200]}></rect>
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  version="1.1"
                  onMouseMove={handleMouseMove}
                  onMouseDown={handleMouseDown}
                  onMouseUp={handleMouseUp}
                >
                  <defs>
                    <pattern
                      id="smallGrid"
                      width={`${GRID_SIZE}px`}
                      height={`${GRID_SIZE}px`}
                      patternUnits="userSpaceOnUse"
                    >
                      <path
                        d={`M ${GRID_SIZE} 0 L 0 0 0 ${GRID_SIZE}`}
                        fill="none"
                        stroke={PALETTE.GRAY[300]}
                        strokeWidth="0.5"
                      />
                    </pattern>
                    <pattern
                      id="grid"
                      width={`${GRID_SIZE * 10}px`}
                      height={`${GRID_SIZE * 10}px`}
                      patternUnits="userSpaceOnUse"
                    >
                      <rect
                        width={`${GRID_SIZE * 10}px`}
                        height={`${GRID_SIZE * 10}px`}
                        fill="url(#smallGrid)"
                      />
                      <path
                        d={`M ${GRID_SIZE * 10} 0 L 0 0 0 ${GRID_SIZE * 10}`}
                        fill="none"
                        stroke={PALETTE.GRAY[300]}
                        strokeWidth="1"
                      />
                    </pattern>
                  </defs>
                  <g
                    id="board"
                    transform={`matrix(${board.scale}, 0, 0, ${board.scale}, ${board.x}, ${board.y})`}
                    onClickCapture={handleClickBoard}
                    onMouseEnter={() => setStickyPointerView(true)}
                    onMouseLeave={() => setStickyPointerView(false)}
                  >
                    <rect width={`${width}px`} height={`${height}px`} fill="white" />

                    {/* 전체 격자를 그리는 rect */}
                    <rect
                      width={`${width + 0.5}px`}
                      height={`${height + 0.5}px`}
                      fill="url(#grid)"
                    />

                    {[Mode.Line, Mode.Square].includes(mode) && stickyPointerView && (
                      <circle
                        cx={stickyCoordinate.x}
                        cy={stickyCoordinate.y}
                        r={3}
                        fill={PALETTE.OPACITY_BLACK[300]}
                        pointerEvents="none"
                      />
                    )}

                    {/* Note: 공간 영역 */}
                    {spaces.map(({ id, color, area, name }) => (
                      <g key={id} pointerEvents="none">
                        <rect
                          x={area.x}
                          y={area.y}
                          width={area.width}
                          height={area.height}
                          fill={color}
                          opacity="0.1"
                        />
                        <text
                          x={area.x + area.width / 2}
                          y={area.y + area.height / 2}
                          dominantBaseline="middle"
                          textAnchor="middle"
                          fill={PALETTE.BLACK[700]}
                          fontSize="1rem"
                          opacity="0.3"
                        >
                          {name}
                        </text>
                      </g>
                    ))}

                    {mapElements.map((element) => (
                      <polyline
                        key={`polyline-${element.id}`}
                        points={element.points.join(' ')}
                        stroke={element.stroke}
                        strokeWidth={LINE_WIDTH}
                        strokeLinecap="round"
                        cursor={mode === Mode.Select ? 'pointer' : 'default'}
                        opacity={erasingMapElementIds.includes(element.id) ? '0.3' : '1'}
                        pointerEvents={isDraggable ? 'none' : 'auto'}
                        onClickCapture={(event) => handleSelectMapElement(event, element.id)}
                        onMouseOverCapture={() => handleSelectErasingElement(element.id)}
                      />
                    ))}

                    {mode === Mode.Select &&
                      gripPoints.map(({ x, y }, index) => (
                        <Styled.GripPoint key={index} cx={x} cy={y} r={4} />
                      ))}

                    {drawingStatus.start && (
                      <polyline
                        key="preview-line"
                        points={`${drawingStatus.start.x},${drawingStatus.start.y} ${stickyCoordinate.x},${stickyCoordinate.y}`}
                        stroke={PALETTE.OPACITY_BLACK[200]}
                        strokeWidth={LINE_WIDTH}
                        strokeLinecap="round"
                      />
                    )}
                  </g>
                </svg>
              </Styled.BoardContainer>
            </Styled.Editor>
            <Styled.Toolbar>
              <Styled.InputWrapper>
                <Styled.Label>
                  <Styled.LabelIcon>W</Styled.LabelIcon>
                  <Styled.LabelText>넓이</Styled.LabelText>
                </Styled.Label>
                <Styled.SizeInput
                  value={widthValue}
                  onChange={onChangeWidthValue}
                  onBlur={handleWidthSize}
                />
              </Styled.InputWrapper>
              <Styled.InputWrapper>
                <Styled.Label>
                  <Styled.LabelIcon>H</Styled.LabelIcon>
                  <Styled.LabelText>높이</Styled.LabelText>
                </Styled.Label>
                <Styled.SizeInput
                  value={heightValue}
                  onChange={onChangeHeightValue}
                  onBlur={handleHeightSize}
                />
              </Styled.InputWrapper>
            </Styled.Toolbar>
          </Styled.EditorContent>
        </Styled.Container>
      </Layout>
    </>
  );
};

export default ManagerMapCreate;
