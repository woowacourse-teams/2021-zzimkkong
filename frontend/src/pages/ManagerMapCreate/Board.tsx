import React, { useEffect, useRef } from 'react';
import { KEY } from 'constants/editor';
import PALETTE from 'constants/palette';
import { EditorBoard } from 'types/common';
import * as Styled from './Board.styles';
import GridPattern from './GridPattern';
import useBindKeyPress from './hooks/useBindKeyPress';
import useBoardMove from './hooks/useBoardMove';
import useBoardZoom from './hooks/useBoardZoom';

interface Props {
  statusState: [EditorBoard, React.Dispatch<React.SetStateAction<EditorBoard>>];
  forceDraggable?: boolean;
  onMouseMove?: (event: React.MouseEvent<SVGElement>) => void;
}

const Board = ({ statusState, forceDraggable = false, onMouseMove }: Props): JSX.Element => {
  const rootSvgRef = useRef<SVGSVGElement | null>(null);
  const [status, setStatus] = statusState;

  const { pressedKey } = useBindKeyPress();
  const isPressSpacebar = pressedKey === KEY.SPACE;
  const isDraggable = isPressSpacebar || forceDraggable;

  const { onWheel } = useBoardZoom(statusState);
  const { isDragging, onDragStart, onDrag, onDragEnd, onMouseOut } = useBoardMove(
    statusState,
    isDraggable
  );

  const handleMouseMove = (event: React.MouseEvent<SVGElement>) => {
    onMouseMove?.(event);
  };

  useEffect(() => {
    const boardWidth = rootSvgRef.current?.clientWidth ?? 0;
    const boardHeight = rootSvgRef.current?.clientHeight ?? 0;

    setStatus((prevStatus) => ({
      ...prevStatus,
      x: (boardWidth - prevStatus.width) / 2,
      y: (boardHeight - prevStatus.height) / 2,
    }));
  }, [setStatus]);

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
      <svg xmlns="http://www.w3.org/2000/svg" version="1.1" onMouseMove={handleMouseMove}>
        <GridPattern.Defs />
        <g
          id="board"
          transform={`matrix(${status.scale}, 0, 0, ${status.scale}, ${status.x}, ${status.y})`}
        >
          <rect width={`${status.width}px`} height={`${status.height}px`} fill="white" />
          <GridPattern width={status.width} height={status.height} />
        </g>
      </svg>
    </Styled.RootSvg>
  );
};

export default Board;
