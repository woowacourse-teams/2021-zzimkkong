import { Dispatch, MouseEventHandler, SetStateAction, useState } from 'react';
import PALETTE from 'constants/palette';
import { Coordinate, EditorBoard } from 'types/common';
import { SpaceEditorMode as Mode } from '../constants';
import * as Styled from './Editor.styles';
import GridPattern from './GridPattern';

interface Props {
  mode: Mode;
  boardState: [EditorBoard, Dispatch<SetStateAction<EditorBoard>>];
}

const Editor = ({ mode, boardState }: Props): JSX.Element => {
  const [movable, setMovable] = useState(false);
  const [moving, setMoving] = useState(false);
  const [dragOffset, setDragOffset] = useState<Coordinate | null>(null);

  const [board, setBoard] = boardState;

  const handleMouseOver = () => {
    if (mode !== Mode.Move) return;

    setMovable(true);
  };

  const handleMouseOut = () => {
    setMovable(false);
    setMoving(false);
  };

  const handleDragStart: MouseEventHandler<SVGElement> = (event) => {
    if (mode !== Mode.Move) return;

    const dragOffsetX = event.nativeEvent.offsetX - board.x;
    const dragOffsetY = event.nativeEvent.offsetY - board.y;

    setMoving(true);
    setDragOffset({ x: dragOffsetX, y: dragOffsetY });
  };

  const handleDragging: MouseEventHandler<SVGElement> = (event) => {
    if (mode !== Mode.Move || !moving || dragOffset === null) return;

    const { offsetX, offsetY } = event.nativeEvent;

    setBoard((prevState) => ({
      ...prevState,
      x: offsetX - dragOffset.x,
      y: offsetY - dragOffset.y,
    }));
  };

  const handleDragEnd = () => {
    setMoving(false);
    setDragOffset(null);
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
        onMouseOver={handleMouseOver}
        onMouseOut={handleMouseOut}
        onMouseDown={handleDragStart}
        onMouseUp={handleDragEnd}
        onMouseMove={handleDragging}
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
          </Styled.BoardGroup>
        </Styled.Board>
      </Styled.BoardContainer>
    </Styled.Editor>
  );
};

export default Editor;
