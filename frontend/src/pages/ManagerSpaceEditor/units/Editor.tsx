import {
  Dispatch,
  MouseEventHandler,
  SetStateAction,
  useCallback,
  useEffect,
  useMemo,
  useState,
} from 'react';
import { EDITOR, KEY } from 'constants/editor';
import PALETTE from 'constants/palette';
import useBindKeyPress from 'hooks/board/useBindKeyPress';
import useBoardCoordinate from 'hooks/board/useBoardCoordinate';
import useFormContext from 'hooks/useFormContext';
import { Area, EditorBoard, ManagerSpace, MapElement } from 'types/common';
import { SpaceEditorMode as Mode } from 'types/editor';
import { drawingModes } from '../data';
import useDrawingRect from '../hooks/useDrawingRect';
import { SpaceFormContext } from '../providers/SpaceFormProvider';
import Board from './Board';
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
  const { stickyRectCoordinate, onMouseMove: updateCoordinate } = useBoardCoordinate(board);

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

  const handleDrawingStart = useCallback(() => {
    if (!isDrawingMode) return;

    setIsDrawing(true);

    if (mode === Mode.Rect) startDrawingRect();
  }, [isDrawingMode, setIsDrawing, mode, startDrawingRect]);

  const handleMouseMove: MouseEventHandler<SVGSVGElement> = useCallback(
    (event) => {
      updateCoordinate(event);

      if (!isDrawingMode || !isDrawing) return;

      if (mode === Mode.Rect) {
        updateRect();
        updateArea(rect as Area);
      }
    },
    [isDrawing, isDrawingMode, mode, rect, updateArea, updateRect, updateCoordinate]
  );

  const handleDrawingEnd = useCallback(() => {
    if (!isDrawingMode || !isDrawing) return;

    setMode(Mode.Form);
    endDrawingRect();
    setIsDrawing(false);
  }, [isDrawing, isDrawingMode, setMode, endDrawingRect]);

  useEffect(() => {
    setMovable(pressedKey === KEY.SPACE);
  }, [movable, pressedKey]);

  return (
    <Board
      movable={movable}
      boardState={boardState}
      onMouseMove={handleMouseMove}
      onMouseDown={handleDrawingStart}
      onMouseUp={handleDrawingEnd}
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

      {isDrawingMode && (
        <BoardCursorRect coordinate={stickyRectCoordinate} size={EDITOR.GRID_SIZE} />
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
