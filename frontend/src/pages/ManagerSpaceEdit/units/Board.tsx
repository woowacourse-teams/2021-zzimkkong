import { Dispatch, ReactNode, SetStateAction, SVGAttributes } from 'react';
import PALETTE from 'constants/palette';
import { EditorBoard } from 'types/common';
import useBoardMove from '../hooks/useBoardMove';
import useBoardZoom from '../hooks/useBoardZoom';
import * as Styled from './Board.styles';
import CursorRect from './CursorRect';
import GridPattern from './GridPattern';
import MapElement from './MapElement';

interface Props extends SVGAttributes<SVGElement> {
  movable: boolean;
  boardState: [EditorBoard, Dispatch<SetStateAction<EditorBoard>>];
  children: ReactNode;
}

const Board = ({ movable, boardState, children, ...props }: Props): JSX.Element => {
  const [board] = boardState;

  const { onWheel } = useBoardZoom(boardState);
  const { moving, onMouseOut, onDragStart, onDrag, onDragEnd } = useBoardMove(boardState, movable);

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
        onMouseOut={onMouseOut}
        onMouseDown={onDragStart}
        onMouseMove={onDrag}
        onMouseUp={onDragEnd}
      >
        <Styled.BoardContainerBackground width="100%" height="100%" fill={PALETTE.GRAY[200]} />

        <Styled.Board xmlns="http://www.w3.org/2000/svg" version="1.1" {...props}>
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

Board.CursorRect = CursorRect;
Board.MapElement = MapElement;

export default Board;
