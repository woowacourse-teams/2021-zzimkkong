import { Dispatch, ReactNode, SetStateAction, useState } from 'react';
import PALETTE from 'constants/palette';
import { EditorBoard } from 'types/common';
import { SpaceEditorMode as Mode } from '../constants';
import useBoardMove from '../hooks/useBoardMove';
import useBoardZoom from '../hooks/useBoardZoom';
import * as Styled from './Editor.styles';
import GridPattern from './GridPattern';

interface Props {
  mode: Mode;
  boardState: [EditorBoard, Dispatch<SetStateAction<EditorBoard>>];
  children: ReactNode;
}

const Editor = ({ mode, boardState, children }: Props): JSX.Element => {
  const [board] = boardState;

  const [movable, setMovable] = useState(false);

  const { onWheel } = useBoardZoom(boardState);
  const { moving, onMouseOut, onDragStart, onDrag, onDragEnd } = useBoardMove(
    boardState,
    mode === Mode.Move
  );

  const handleMouseOver = () => {
    if (mode !== Mode.Move) return;

    setMovable(false);
  };

  const handleMouseOut = () => {
    onMouseOut();
    setMovable(false);
  };

  return (
    <Styled.Editor>
      <Styled.BoardContainer
        xmlns="http://www.w3.org/2000/svg"
        version="1.1"
        width="100%"
        height="100%"
        isDragging={moving}
        isDraggable={movable}
        onWheel={onWheel}
        onMouseOver={handleMouseOver}
        onMouseOut={handleMouseOut}
        onMouseDown={onDragStart}
        onMouseMove={onDrag}
        onMouseUp={onDragEnd}
      >
        <Styled.BoardContainerBackground width="100%" height="100%" fill={PALETTE.GRAY[200]} />

        <Styled.Board>
          <GridPattern.Defs />

          <Styled.BoardGroup
            id="board"
            transform={`matrix(${board.scale}, 0, 0, ${board.scale}, ${board.x}, ${board.y})`}
          >
            <Styled.BoardBackground
              width={`${board.width}px`}
              height={`${board.height}px`}
              fill="white"
            />

            <GridPattern width={board.width} height={board.height} />

            {children}
          </Styled.BoardGroup>
        </Styled.Board>
      </Styled.BoardContainer>
    </Styled.Editor>
  );
};

export default Editor;
