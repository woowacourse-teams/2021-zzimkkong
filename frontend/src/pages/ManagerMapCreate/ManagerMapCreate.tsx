import { AxiosError } from 'axios';
import {
  FormEventHandler,
  MouseEventHandler,
  useEffect,
  useRef,
  useState,
  WheelEventHandler,
} from 'react';
import { useMutation } from 'react-query';
import { postMap } from 'api/map';
import { ReactComponent as EditIcon } from 'assets/svg/edit.svg';
import { ReactComponent as ItemsIcon } from 'assets/svg/items.svg';
import { ReactComponent as LineIcon } from 'assets/svg/line.svg';
import { ReactComponent as MoveIcon } from 'assets/svg/move.svg';
import { ReactComponent as PolylineIcon } from 'assets/svg/polyline.svg';
import { ReactComponent as SelectIcon } from 'assets/svg/select.svg';
import Button from 'components/Button/Button';
import Header from 'components/Header/Header';
import IconButton from 'components/IconButton/IconButton';
import Layout from 'components/Layout/Layout';
import PALETTE from 'constants/palette';
import useInput from 'hooks/useInput';
import { Color, Coordinate, DrawingStatus } from 'types/common';
import { Mode } from 'types/editor';
import { ErrorResponse } from 'types/response';
import * as Styled from './ManagerMapCreate.styles';

interface MapElement {
  id: number;
  type: 'polyline';
  stroke: Color;
  points: string[];
}

const GRID_SIZE = 10;
const SCALE_DELTA = 0.001;
const MIN_SCALE = 0.5;
const MAX_SCALE = 3.0;

const ManagerMapCreate = (): JSX.Element => {
  const editorRef = useRef<HTMLDivElement | null>(null);

  const [mapName, onChangeMapName] = useInput('');

  const [mode, setMode] = useState(Mode.Select);
  const [isDragging, setDragging] = useState(false);
  const [dragOffsetX, setDragOffsetX] = useState(0);
  const [dragOffsetY, setDragOffsetY] = useState(0);

  const [coordinate, setCoordinate] = useState<Coordinate>({ x: 0, y: 0 });
  const stickyCoordinate: Coordinate = {
    x: Math.round(coordinate.x / GRID_SIZE) * GRID_SIZE,
    y: Math.round(coordinate.y / GRID_SIZE) * GRID_SIZE,
  };

  const [color, setColor] = useState<Color>('#333333');
  const [drawingStatus, setDrawingStatus] = useState<DrawingStatus>({});
  const [mapElements, setMapElements] = useState<MapElement[]>([]);
  const nextId = Math.max(...mapElements.map(({ id }) => id), 1) + 1;

  const [widthValue, onChangeWidthValue] = useInput('800');
  const [heightValue, onChangeHeightValue] = useInput('600');

  const width = Number(widthValue);
  const height = Number(heightValue);

  const [board, setBoard] = useState({
    width,
    height,
    x: 0,
    y: 0,
    scale: 1,
  });

  const getSVGCoordinate = (event: React.MouseEvent<SVGElement>) => {
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
    setDragOffsetX(event.nativeEvent.offsetX - board.x);
    setDragOffsetY(event.nativeEvent.offsetY - board.y);

    setDragging(true);
  };

  const handleDrag: MouseEventHandler<SVGElement> = (event) => {
    if (mode !== Mode.Move || !isDragging) return;

    const { offsetX, offsetY } = event.nativeEvent;

    setBoard((prevState) => ({
      ...prevState,
      x: offsetX - dragOffsetX,
      y: offsetY - dragOffsetY,
    }));
  };

  const handleDragEnd = () => {
    setDragOffsetX(0);
    setDragOffsetY(0);

    setDragging(false);
  };

  const handleDrawStart: MouseEventHandler<SVGElement> = (event) => {
    if (mode !== Mode.Line) return;

    setDrawingStatus((prevState) => ({
      ...prevState,
      start: stickyCoordinate,
    }));
  };

  const handleDrawEnd: MouseEventHandler<SVGElement> = (event) => {
    if (mode !== Mode.Line) return;

    if (!drawingStatus || !drawingStatus.start) return;

    const startPoint = `${drawingStatus.start.x},${drawingStatus.start.y}`;
    const endPoint = `${stickyCoordinate.x},${stickyCoordinate.y}`;

    setMapElements((prevState) => [
      ...prevState,
      {
        id: nextId,
        type: 'polyline',
        stroke: color,
        points: [startPoint, endPoint],
      },
    ]);

    setDrawingStatus({});
  };

  const createMap = useMutation(postMap, {
    onSuccess: () => {
      alert('맵 생성 완료');
    },
    onError: (error: AxiosError<ErrorResponse>) => {
      console.error(error);
    },
  });

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
                strokeWidth='2'
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
                  <IconButton>
                    <EditIcon />
                  </IconButton>
                </Styled.MapNameContainer>
                <Styled.TempSaveContainer>
                  <Styled.TempSaveMessage>1분 전에 임시 저장되었습니다.</Styled.TempSaveMessage>
                  <Styled.TempSaveButton variant="primary-text">임시 저장</Styled.TempSaveButton>
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
                onClick={() => setMode(Mode.Select)}
              >
                <SelectIcon />
              </Styled.ToolbarButton>
              <Styled.ToolbarButton
                text="이동"
                selected={mode === Mode.Move}
                onClick={() => setMode(Mode.Move)}
              >
                <MoveIcon />
              </Styled.ToolbarButton>
              <Styled.ToolbarButton
                text="선"
                selected={mode === Mode.Line}
                onClick={() => setMode(Mode.Line)}
              >
                <LineIcon />
              </Styled.ToolbarButton>
              <Styled.ToolbarButton
                text="다각선"
                selected={mode === Mode.Polyline}
                onClick={() => setMode(Mode.Polyline)}
              >
                <PolylineIcon />
              </Styled.ToolbarButton>
              <Styled.ToolbarButton
                text="장식"
                selected={mode === Mode.Decoration}
                onClick={() => setMode(Mode.Decoration)}
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
                isDraggable={mode === Mode.Move}
                onWheel={handleWheel}
                onMouseDown={handleDragStart}
                onMouseUp={handleDragEnd}
                onMouseMoveCapture={handleDrag}
                tabIndex={0}
              >
                <rect width="100%" height="100%" fill={PALETTE.GRAY[200]}></rect>
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  version="1.1"
                  onMouseMove={handleMouseMove}
                  onMouseDown={handleDrawStart}
                  onMouseUp={handleDrawEnd}
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
                  >
                    <rect width={`${width}px`} height={`${height}px`} fill="white" />
                    {/* 전체 격자를 그리는 rect */}
                    <rect
                      width={`${width + 0.5}px`}
                      height={`${height + 0.5}px`}
                      fill="url(#grid)"
                    />
                    <circle
                      cx={stickyCoordinate.x}
                      cy={stickyCoordinate.y}
                      r={4}
                      fill={PALETTE.OPACITY_BLACK[700]}
                    />
                    {mapElements.map((element) => (
                      <polyline
                        key={`polyline-${element.id}`}
                        points={element.points.join(' ')}
                        stroke={element.stroke}
                        strokeWidth="2"
                      />
                    ))}
                    {drawingStatus.start && (
                      <polyline
                        key="preview-line"
                        points={`${drawingStatus.start.x},${drawingStatus.start.y} ${stickyCoordinate.x},${stickyCoordinate.y}`}
                        stroke={PALETTE.OPACITY_BLACK[200]}
                        strokeWidth="2"
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
