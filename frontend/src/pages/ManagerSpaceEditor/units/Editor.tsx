import {
  Dispatch,
  MouseEventHandler,
  SetStateAction,
  useCallback,
  useEffect,
  useMemo,
  useState,
} from 'react';
import Board from 'components/Board/Board';
import { EDITOR, KEY } from 'constants/editor';
import PALETTE from 'constants/palette';
import useBindKeyPress from 'hooks/board/useBindKeyPress';
import useBoardCoordinate from 'hooks/board/useBoardCoordinate';
import useBoardMove from 'hooks/board/useBoardMove';
import useBoardZoom from 'hooks/board/useBoardZoom';
import useFormContext from 'hooks/useFormContext';
import { Area, Coordinate, EditorBoard, ManagerSpace, MapElement } from 'types/common';
import { DrawingAreaShape, SpaceEditorMode as Mode } from 'types/editor';
import { drawingModes } from '../data';
import useDrawingRect from '../hooks/useDrawingRect';
import { SpaceFormContext } from '../providers/SpaceFormProvider';
import BoardCursorDot from './BoardCursorDot';
import BoardCursorRect from './BoardCursorRect';
import BoardMapElement from './BoardMapElement';
import BoardSpace from './BoardSpace';

interface Props {
  modeState: [Mode, Dispatch<SetStateAction<Mode>>];
  boardState: [EditorBoard, Dispatch<SetStateAction<EditorBoard>>];
  selectedSpaceIdState: [number | null, Dispatch<SetStateAction<number | null>>];
  mapElements: MapElement[];
  spaces: ManagerSpace[];
}

const Editor = ({
  modeState,
  boardState,
  selectedSpaceIdState,
  mapElements,
  spaces,
}: Props): JSX.Element => {
  const [board] = boardState;
  const [mode, setMode] = modeState;
  const [selectedSpaceId, setSelectedSpaceId] = selectedSpaceIdState;

  const { pressedKey } = useBindKeyPress();
  const [movable, setMovable] = useState(pressedKey === KEY.SPACE);

  const { values, updateWithSpace, updateArea } = useFormContext(SpaceFormContext);
  const {
    stickyDotCoordinate,
    stickyRectCoordinate,
    onMouseMove: updateCoordinate,
  } = useBoardCoordinate(board);

  const { onWheel } = useBoardZoom(boardState);
  const { isMoving, onDragStart, onDrag, onDragEnd, onMouseOut } = useBoardMove(
    boardState,
    movable
  );

  const { rect, startDrawingRect, updateRect, endDrawingRect } =
    useDrawingRect(stickyRectCoordinate);
  const [isDrawing, setIsDrawing] = useState(false);

  const isDrawingMode = useMemo(() => drawingModes.includes(mode) && !movable, [mode, movable]);

  const unSelectedSpaces = useMemo(() => {
    if (selectedSpaceId === null) return spaces;

    return spaces.filter(({ id }) => id !== selectedSpaceId);
  }, [spaces, selectedSpaceId]);

  const handleClickSpace = useCallback(
    (spaceId: number) => {
      if (isDrawingMode) return;

      const selectedSpace = spaces.find((space) => space.id === spaceId);

      updateWithSpace(selectedSpace as ManagerSpace);
      setSelectedSpaceId(spaceId);
    },
    [isDrawingMode, spaces, setSelectedSpaceId, updateWithSpace]
  );

  // TODO POLYGON START

  const [polygon, setPolygon] = useState<Area | null>(null);
  const [points, setPoints] = useState<Coordinate[]>([]);
  const [startPoint, setStartPoint] = useState<Coordinate | null>(null);
  const [currentPoint, setCurrentPoint] = useState<Coordinate | null>(null);

  const startDrawingPolygon = useCallback(() => {
    setPoints([stickyDotCoordinate]);
    setStartPoint(stickyDotCoordinate);
    setCurrentPoint(stickyDotCoordinate);
  }, [stickyDotCoordinate]);

  const updatePolygon = useCallback(() => {
    setPoints([...points, stickyDotCoordinate]);
    setCurrentPoint(stickyDotCoordinate);
    setPolygon({
      shape: DrawingAreaShape.Polygon,
      points: [...points, stickyDotCoordinate],
    });
    updateArea(polygon as Area);
  }, [points, polygon, stickyDotCoordinate, updateArea]);

  const endDrawingPolygon = useCallback(() => {
    if (startPoint?.x !== stickyDotCoordinate.x || startPoint?.y !== stickyDotCoordinate.y) return;
    if (points.length < 3) return;

    // 초기화
    setPoints([]);
    setStartPoint(null);
    setCurrentPoint(null);

    setMode(Mode.Form);
    setIsDrawing(false);
  }, [setMode, startPoint, stickyDotCoordinate, points]);

  // TODO POLYGON END

  useEffect(() => {
    console.log('points : ', points);
    console.log('startPoint : ', startPoint);
    console.log('currentPoint : ', currentPoint);
    console.log(
      points.map((point) => `${point.x},${point.y}`).join(' ') +
        `${stickyDotCoordinate.x},${stickyDotCoordinate.y}`
    );
  }, [currentPoint, points, startPoint, stickyDotCoordinate.x, stickyDotCoordinate.y]);

  const handleMouseDown = useCallback(() => {
    if (!isDrawingMode) return;

    setIsDrawing(true);

    if (mode === Mode.Rect) startDrawingRect();
    else if (mode === Mode.Polygon) startPoint === null ? startDrawingPolygon() : updatePolygon();
  }, [updatePolygon, isDrawingMode, mode, startDrawingPolygon, startDrawingRect, startPoint]);

  const handleMouseMove: MouseEventHandler<SVGSVGElement> = useCallback(
    (event) => {
      updateCoordinate(event);

      if (!isDrawingMode || !isDrawing) return;

      if (mode === Mode.Rect) {
        updateRect();
        updateArea(rect as Area);
      }
    },
    [updateCoordinate, isDrawingMode, isDrawing, mode, updateRect, updateArea, rect]
  );

  const handleMouseUp = useCallback(() => {
    if (!isDrawingMode || !isDrawing) return;

    if (mode === Mode.Rect) endDrawingRect();
    else if (mode === Mode.Polygon) {
      endDrawingPolygon();
      return;
    }

    setMode(Mode.Form);
    setIsDrawing(false);
  }, [isDrawingMode, isDrawing, mode, endDrawingRect, endDrawingPolygon, setMode]);

  useEffect(() => {
    setMovable(pressedKey === KEY.SPACE);
  }, [movable, pressedKey]);

  return (
    <Board
      boardState={boardState}
      movable={movable}
      isMoving={isMoving}
      onDragStart={onDragStart}
      onDrag={onDrag}
      onDragEnd={onDragEnd}
      onMouseOut={onMouseOut}
      onMouseMove={handleMouseMove}
      onMouseDown={handleMouseDown}
      onMouseUp={handleMouseUp}
      onWheel={onWheel}
    >
      {values.area && (
        <BoardSpace
          space={{
            name: values.name,
            color: isDrawing ? PALETTE.GRAY[400] : values.color,
            area: values.area,
          }}
          drawing={true}
          selected={true}
        />
      )}

      {isDrawingMode && mode === Mode.Rect && (
        <BoardCursorRect coordinate={stickyRectCoordinate} size={EDITOR.GRID_SIZE} />
      )}

      {isDrawingMode && mode === Mode.Polygon && (
        <>
          <BoardCursorDot coordinate={stickyDotCoordinate} />
          {currentPoint && (
            <polygon
              points={
                points.map(({ x, y }) => `${x},${y}`).join(' ') +
                ` ${stickyDotCoordinate.x},${stickyDotCoordinate.y}`
              }
              stroke={EDITOR.STROKE_PREVIEW}
              fill={EDITOR.POLYGON_PREVIEW}
              strokeWidth={EDITOR.STROKE_WIDTH}
              pointerEvents="none"
            />
          )}
        </>
      )}

      {unSelectedSpaces?.map((space, index) => (
        <BoardSpace
          key={`space-${index}`}
          space={space}
          drawing={isDrawingMode}
          selected={space.id === selectedSpaceId}
          onClick={() => handleClickSpace(space.id)}
        />
      ))}

      {mapElements?.map((element, index) => (
        <BoardMapElement key={`map-${index}`} mapElement={element} />
      ))}
    </Board>
  );
};

export default Editor;
