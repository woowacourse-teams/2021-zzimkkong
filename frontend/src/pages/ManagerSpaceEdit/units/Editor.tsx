import { Dispatch, MouseEventHandler, SetStateAction, useEffect, useState } from 'react';
import { EDITOR, KEY } from 'constants/editor';
import PALETTE from 'constants/palette';
import { Area, EditorBoard, ManagerSpace, MapElement } from 'types/common';
import { SpaceEditorMode as Mode } from '../constants';
import { drawingModes } from '../data';
import useBindKeyPress from '../hooks/useBindKeyPress';
import useBoardCoordinate from '../hooks/useBoardCoordinate';
import useDrawingRect from '../hooks/useDrawingRect';
import useFormContext from '../hooks/useFormContext';
import { SpaceFormContext } from '../providers/SpaceFormProvider';
import Board from './Board';

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

  const { values, setArea } = useFormContext(SpaceFormContext);
  const { stickyCoordinate, onMouseMove: updateCoordinate } = useBoardCoordinate(board);

  const { rect, startDrawingRect, updateRect, endDrawingRect } = useDrawingRect(stickyCoordinate);
  const [isDrawing, setIsDrawing] = useState(false);

  const isDrawingMode = drawingModes.includes(mode) && !movable;

  const handleClickSpace = (id: number) => {
    if (isDrawingMode) return;

    setSelectedSpaceId(id);
  };

  const handleDrawingStart = () => {
    if (!isDrawingMode) return;

    setIsDrawing(true);

    if (mode === Mode.Rect) startDrawingRect();
  };

  const handleMouseMove: MouseEventHandler<SVGElement> = (event) => {
    updateCoordinate(event);

    if (!isDrawingMode || !isDrawing) return;

    if (mode === Mode.Rect) {
      updateRect();
      setArea(rect as Area);
    }
  };

  const handleDrawingEnd = () => {
    if (!isDrawingMode || !isDrawing) return;

    setMode(Mode.Form);
    endDrawingRect();
    setIsDrawing(false);
  };

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
        <Board.Space
          space={{
            name: values.name,
            color: isDrawing ? PALETTE.GRAY[400] : values.color,
            area: values.area,
          }}
          drawing={true}
          selected={true}
        />
      )}

      {isDrawingMode && <Board.CursorRect coordinate={stickyCoordinate} size={EDITOR.GRID_SIZE} />}

      {spaces?.map((space, index) => (
        <Board.Space
          key={`space-${index}`}
          space={
            space.id === selectedSpaceId
              ? { ...space, name: values.name, color: values.color }
              : space
          }
          drawing={isDrawingMode}
          selected={space.id === selectedSpaceId}
          onClick={() => handleClickSpace(space.id)}
        />
      ))}

      {mapElements?.map((element, index) => (
        <Board.MapElement key={`map-${index}`} mapElement={element} />
      ))}
    </Board>
  );
};

export default Editor;
