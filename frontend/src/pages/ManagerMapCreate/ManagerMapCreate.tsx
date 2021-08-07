import { AxiosError } from 'axios';
import {
  FormEventHandler,
  MouseEvent,
  MouseEventHandler,
  useCallback,
  useEffect,
  useRef,
  useState,
  WheelEventHandler,
} from 'react';
import { useMutation } from 'react-query';
import { useHistory } from 'react-router-dom';
import { postMap } from 'api/map';
import { ReactComponent as ItemsIcon } from 'assets/svg/items.svg';
import { ReactComponent as LineIcon } from 'assets/svg/line.svg';
import { ReactComponent as MoveIcon } from 'assets/svg/move.svg';
import { ReactComponent as PolylineIcon } from 'assets/svg/polyline.svg';
import { ReactComponent as SelectIcon } from 'assets/svg/select.svg';
import Button from 'components/Button/Button';
import Header from 'components/Header/Header';
import Layout from 'components/Layout/Layout';
import PALETTE from 'constants/palette';
import PATH from 'constants/path';
import useInput from 'hooks/useInput';
import { Color, Coordinate, DrawingStatus, EditorBoard, GripPoint, MapElement } from 'types/common';
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

const ManagerMapCreate = (): JSX.Element => {
  const editorRef = useRef<HTMLDivElement | null>(null);

  const history = useHistory();

  const [mapName, onChangeMapName] = useInput('');

  const [mode, setMode] = useState(Mode.Select);
  const [dragOffsetX, setDragOffsetX] = useState(0);
  const [dragOffsetY, setDragOffsetY] = useState(0);
  const [isDragging, setDragging] = useState(false);
  const [isPressSpacebar, setPressSpacebar] = useState(false);
  const isDraggable = mode === Mode.Move || isPressSpacebar;

  const [coordinate, setCoordinate] = useState<Coordinate>({ x: 0, y: 0 });
  const stickyCoordinate: Coordinate = {
    x: Math.round(coordinate.x / GRID_SIZE) * GRID_SIZE,
    y: Math.round(coordinate.y / GRID_SIZE) * GRID_SIZE,
  };

  const [color, setColor] = useState<Color>('#333333');
  const [drawingStatus, setDrawingStatus] = useState<DrawingStatus>({});
  const [mapElements, setMapElements] = useState<MapElement[]>([]);

  const [gripPoints, setGripPoints] = useState<GripPoint[]>([]);
  const [selectedMapElementId, setSelectedMapElementId] = useState<MapElement['id'] | null>(null);
  const [erasingMapElementIds, setErasingMapElementIds] = useState<MapElement['id'][]>([]);
  const [isErasing, setErasing] = useState(false);

  const nextMapElementId = Math.max(...mapElements.map(({ id }) => id), 1) + 1;
  const nextGripPointId = Math.max(...gripPoints.map(({ id }) => id), 1) + 1;

  const [widthValue, onChangeWidthValue] = useInput('800');
  const [heightValue, onChangeHeightValue] = useInput('600');

  const width = Number(widthValue);
  const height = Number(heightValue);

  const [board, setBoard] = useState<EditorBoard>({
    width,
    height,
    x: 0,
    y: 0,
    scale: 1,
  });

  const createMap = useMutation(postMap, {
    onSuccess: (response) => {
      if (window.confirm('맵 생성 완료! 공간을 편집하러 가시겠어요?')) {
        const headers = response.headers as { location: string };
        const mapId = headers.location.split('/').pop() ?? '';

        history.push(`/map/${mapId}/space/edit`);

        return;
      }

      history.push(PATH.MANAGER_MAIN);
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

  const handleSelectMapElement = (event: MouseEvent<SVGPolylineElement>, id: MapElement['id']) => {
    if (mode !== Mode.Select) return;

    const target = event.target as SVGPolylineElement;
    const points = Object.values<Coordinate>(target?.points).map(({ x, y }) => ({ x, y }));

    const newGripPoints = points.map((point, index) => ({
      id: nextGripPointId + index,
      mapElementId: id,
      x: point.x,
      y: point.y,
    }));

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
    if (mode === Mode.Line) drawStart();
    if (mode === Mode.Eraser) eraseStart();
  };

  const handleMouseUp = () => {
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

    createMap.mutate({ mapName, mapDrawing, mapImageSvg });
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
                  <Button type="button" variant="text">
                    취소
                  </Button>
                  <Button variant="primary">완료</Button>
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
                text="다각선"
                selected={mode === Mode.Polyline}
                onClick={() => selectMode(Mode.Polyline)}
              >
                <PolylineIcon />
              </Styled.ToolbarButton>
              <Styled.ToolbarButton
                text="지우개"
                selected={mode === Mode.Eraser}
                onClick={() => selectMode(Mode.Eraser)}
              >
                <SelectIcon />
              </Styled.ToolbarButton>
              <Styled.ToolbarButton
                text="장식"
                selected={mode === Mode.Decoration}
                onClick={() => selectMode(Mode.Decoration)}
              >
                <ItemsIcon />
              </Styled.ToolbarButton>
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
                  >
                    <rect width={`${width}px`} height={`${height}px`} fill="white" />

                    {/* 전체 격자를 그리는 rect */}
                    <rect
                      width={`${width + 0.5}px`}
                      height={`${height + 0.5}px`}
                      fill="url(#grid)"
                    />

                    {mode === Mode.Line && (
                      <circle
                        cx={stickyCoordinate.x}
                        cy={stickyCoordinate.y}
                        r={4}
                        fill={PALETTE.OPACITY_BLACK[700]}
                      />
                    )}

                    {mapElements.map((element) => (
                      <polyline
                        key={`polyline-${element.id}`}
                        points={element.points.join(' ')}
                        stroke={element.stroke}
                        strokeWidth={LINE_WIDTH}
                        strokeLinecap="round"
                        cursor={mode === Mode.Select ? 'pointer' : 'default'}
                        opacity={erasingMapElementIds.includes(element.id) ? '0.3' : '1'}
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
                <Styled.SizeInput value={widthValue} onChange={onChangeWidthValue} />
              </Styled.InputWrapper>
              <Styled.InputWrapper>
                <Styled.Label>
                  <Styled.LabelIcon>H</Styled.LabelIcon>
                  <Styled.LabelText>높이</Styled.LabelText>
                </Styled.Label>
                <Styled.SizeInput value={heightValue} onChange={onChangeHeightValue} />
              </Styled.InputWrapper>
            </Styled.Toolbar>
          </Styled.EditorContent>
        </Styled.Container>
      </Layout>
    </>
  );
};

export default ManagerMapCreate;
