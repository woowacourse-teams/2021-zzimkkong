import { Dispatch, SetStateAction } from 'react';
import { EDITOR } from 'constants/editor';
import { EditorBoard, MapElement } from 'types/common';
import { SpaceEditorMode as Mode } from '../constants';
import useBoardCoordinate from '../hooks/useBoardCoordinate';
import Board from './Board';

interface Props {
  mode: Mode;
  boardState: [EditorBoard, Dispatch<SetStateAction<EditorBoard>>];
  mapElements: MapElement[];
}

const Editor = ({ mode, boardState, mapElements }: Props): JSX.Element => {
  const [board, setBoard] = boardState;

  const { coordinate, stickyCoordinate, onMouseMove } = useBoardCoordinate(board);

  return (
    <Board moveMode={mode === Mode.Move} boardState={boardState} onMouseMove={onMouseMove}>
      <Board.CursorRect coordinate={stickyCoordinate} size={EDITOR.GRID_SIZE} />
      <Board.MapElement mapElements={mapElements} />
    </Board>
  );
};

export default Editor;
