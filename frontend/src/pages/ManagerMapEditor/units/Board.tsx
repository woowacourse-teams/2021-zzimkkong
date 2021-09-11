import React, { PropsWithChildren, useLayoutEffect, useRef } from 'react';
import PALETTE from 'constants/palette';
import { EditorBoard } from 'types/common';
import * as Styled from './Board.styles';
import GridPattern from './GridPattern';

interface Props {
  statusState: [EditorBoard, React.Dispatch<React.SetStateAction<EditorBoard>>];
  isDraggable?: boolean;
  isDragging?: boolean;
  onClick?: (event: React.MouseEvent<SVGSVGElement>) => void;
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
  statusState,
  isDraggable = false,
  isDragging = false,
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
  const [status, setStatus] = statusState;

  const handleMouseMove = (event: React.MouseEvent<SVGSVGElement>) => {
    onMouseMove?.(event);
  };

  useLayoutEffect(() => {
    const boardWidth = rootSvgRef.current?.clientWidth ?? 0;
    const boardHeight = rootSvgRef.current?.clientHeight ?? 0;

    setStatus((prevStatus) => ({
      ...prevStatus,
      x: (boardWidth - status.width) / 2,
      y: (boardHeight - status.height) / 2,
    }));
  }, [setStatus, status.height, status.width]);

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
      isDraggable={isDraggable}
      isDragging={isDragging}
      ref={rootSvgRef}
    >
      <rect width="100%" height="100%" fill={PALETTE.GRAY[200]} />
      <svg
        xmlns="http://www.w3.org/2000/svg"
        version="1.1"
        onMouseMove={handleMouseMove}
        onMouseDown={onMouseDown}
        onMouseUp={onMouseUp}
      >
        <GridPattern.Defs />
        <g
          id="board"
          transform={`matrix(${status.scale}, 0, 0, ${status.scale}, ${status.x}, ${status.y})`}
          onClickCapture={onClick}
        >
          <rect width={`${status.width}px`} height={`${status.height}px`} fill="white" />
          <GridPattern width={status.width} height={status.height} />
          {children}
        </g>
      </svg>
    </Styled.RootSvg>
  );
};

export default Board;