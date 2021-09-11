import { Dispatch, SetStateAction, useEffect, useState } from 'react';
import { EDITOR, KEY } from 'constants/editor';
import { EditorBoard, ManagerSpace, MapElement } from 'types/common';
import { SpaceEditorMode as Mode } from '../constants';
import useBindKeyPress from '../hooks/useBindKeyPress';
import useBoardCoordinate from '../hooks/useBoardCoordinate';
import Board from './Board';

interface Props {
  mode: Mode;
  boardState: [EditorBoard, Dispatch<SetStateAction<EditorBoard>>];
  mapElements: MapElement[];
  spaces: ManagerSpace[];
}

const Editor = ({ mode, boardState, mapElements, spaces }: Props): JSX.Element => {
  const [board] = boardState;

  const { pressedKey } = useBindKeyPress();
  const [movable, setMovable] = useState(pressedKey === KEY.SPACE);

  const { coordinate, stickyCoordinate, onMouseMove } = useBoardCoordinate(board);

  const isDrawing = mode === Mode.Rect && !movable;

  useEffect(() => {
    setMovable(pressedKey === KEY.SPACE);
  }, [movable, pressedKey]);

  return (
    <Board movable={movable} boardState={boardState} onMouseMove={onMouseMove}>
      {isDrawing && <Board.CursorRect coordinate={stickyCoordinate} size={EDITOR.GRID_SIZE} />}

      {mapElements?.map((element, index) => (
        <Board.MapElement key={`map-${index}`} mapElement={element} />
      ))}
    </Board>
  );
};

export default Editor;
