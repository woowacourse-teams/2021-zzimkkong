import { Dispatch, MouseEventHandler, SetStateAction, useState } from 'react';
import { Coordinate, EditorBoard } from 'types/common';

const useBoardMove = (
  boardState: [EditorBoard, Dispatch<SetStateAction<EditorBoard>>],
  moveMode: boolean
): {
  moving: boolean;
  onMouseOut: () => void;
  onDragStart: (event: React.MouseEvent<SVGElement>) => void;
  onDrag: (event: React.MouseEvent<SVGElement>) => void;
  onDragEnd: () => void;
} => {
  const [board, setBoard] = boardState;

  const [moving, setMoving] = useState(false);
  const [dragOffset, setDragOffset] = useState<Coordinate | null>(null);

  const onMouseOut = () => {
    setMoving(false);
  };

  const onDragStart: MouseEventHandler<SVGElement> = (event) => {
    if (!moveMode) return;

    const dragOffsetX = event.nativeEvent.offsetX - board.x;
    const dragOffsetY = event.nativeEvent.offsetY - board.y;

    setMoving(true);
    setDragOffset({ x: dragOffsetX, y: dragOffsetY });
  };

  const onDrag: MouseEventHandler<SVGElement> = (event) => {
    if (!moveMode || !moving || dragOffset === null) return;

    const { offsetX, offsetY } = event.nativeEvent;

    setBoard((prevState) => ({
      ...prevState,
      x: offsetX - dragOffset.x,
      y: offsetY - dragOffset.y,
    }));
  };

  const onDragEnd = () => {
    setMoving(false);
    setDragOffset(null);
  };

  return {
    moving,
    onMouseOut,
    onDragStart,
    onDrag,
    onDragEnd,
  };
};

export default useBoardMove;
