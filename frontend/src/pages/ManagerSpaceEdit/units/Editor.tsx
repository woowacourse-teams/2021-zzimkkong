import { Dispatch, SetStateAction } from 'react';
import { EditorBoard } from 'types/common';
import { SpaceEditorMode as Mode } from '../constants';
import Board from './Board';

interface Props {
  mode: Mode;
  boardState: [EditorBoard, Dispatch<SetStateAction<EditorBoard>>];
}

const Editor = ({ mode, boardState }: Props): JSX.Element => {
  return (
    <Board moveMode={mode === Mode.Move} boardState={boardState}>
      맵 & 공간 요소
    </Board>
  );
};

export default Editor;
