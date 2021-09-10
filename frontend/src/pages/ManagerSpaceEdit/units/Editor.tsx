import { Dispatch, SetStateAction } from 'react';
import { EditorBoard, MapElement } from 'types/common';
import { SpaceEditorMode as Mode } from '../constants';
import Board from './Board';

interface Props {
  mode: Mode;
  boardState: [EditorBoard, Dispatch<SetStateAction<EditorBoard>>];
  mapElements: MapElement[];
}

const Editor = ({ mode, boardState, mapElements }: Props): JSX.Element => {
  return (
    <Board moveMode={mode === Mode.Move} boardState={boardState}>
      <Board.MapElement mapElements={mapElements} />
    </Board>
  );
};

export default Editor;
