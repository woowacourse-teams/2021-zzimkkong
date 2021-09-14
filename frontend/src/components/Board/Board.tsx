import React, { PropsWithChildren, useLayoutEffect, useRef } from 'react';
import PALETTE from 'constants/palette';
import { EditorBoard } from 'types/common';
import * as Styled from './Board.styles';
import GridPattern from './GridPattern';

interface Props {
  boardState: [EditorBoard, React.Dispatch<React.SetStateAction<EditorBoard>>];
  movable?: boolean;
  isMoving?: boolean;
  onClick?: (event: React.MouseEvent<SVGGElement>) => void;
  onMouseMove?: (event: React.MouseEvent<SVGSVGElement>) => void;
  onMouseDown?: (event: React.MouseEvent<SVGSVGElement>) => void;
  onMouseUp?: (event: React.MouseEvent<SVGSVGElement>) => void;
  onDragStart?: (event: React.MouseEvent<SVGSVGElement>) => void;
  onDrag?: (event: React.MouseEvent<SVGSVGElement>) => void;
  onDragEnd?: (event: React.MouseEvent<SVGSVGElement>) => void;
  onMouseOut?: (event: React.MouseEvent<SVGSVGElement>) => void;
  onWheel?: (event: React.WheelEvent<SVGSVGElement>) => void;
}

const Board = ({
  boardState,
  movable = false,
  isMoving = false,
  onClick,
  onMouseMove,
  onMouseDown,
  onMouseUp,
  onDragStart,
  onDrag,
  onDragEnd,
  onMouseOut,
  onWheel,
  children,
}: PropsWithChildren<Props>): JSX.Element => {
  const rootSvgRef = useRef<SVGSVGElement | null>(null);
  const [board, setBoard] = boardState;

  const handleMouseMove = (event: React.MouseEvent<SVGSVGElement>) => {
    onMouseMove?.(event);
  };

  useLayoutEffect(() => {
    const boardWidth = rootSvgRef.current?.clientWidth ?? 0;
    const boardHeight = rootSvgRef.current?.clientHeight ?? 0;

    setBoard((prevStatus) => ({
      ...prevStatus,
      x: (boardWidth - board.width) / 2,
      y: (boardHeight - board.height) / 2,
    }));
  }, [setBoard, board.height, board.width]);

  return (
    <Styled.RootSvg
      xmlns="http://www.w3.org/2000/svg"
      version="1.1"
      width="100%"
      height="100%"
      onWheel={onWheel}
      onMouseDown={onDragStart}
      onMouseMove={onDrag}
      onMouseUp={onDragEnd}
      onMouseOut={onMouseOut}
      movable={movable}
      isMoving={isMoving}
      ref={rootSvgRef}
    >
      <Styled.BoardContainerBackground width="100%" height="100%" fill={PALETTE.GRAY[200]} />

      <Styled.Board
        xmlns="http://www.w3.org/2000/svg"
        version="1.1"
        onMouseMove={handleMouseMove}
        onMouseDown={onMouseDown}
        onMouseUp={onMouseUp}
      >
        <GridPattern.Defs />

        <Styled.BoardGroup
          id="board"
          transform={`matrix(${board.scale}, 0, 0, ${board.scale}, ${board.x}, ${board.y})`}
          onClickCapture={onClick}
        >
          <Styled.BoardBackground
            width={`${board.width}px`}
            height={`${board.height}px`}
            fill={PALETTE.WHITE}
          />

          <GridPattern width={board.width} height={board.height} />

          {children}
        </Styled.BoardGroup>
      </Styled.Board>
    </Styled.RootSvg>
  );
};

export default Board;
