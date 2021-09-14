import { useState } from 'react';
import { Coordinate, EditorBoard } from 'types/common';

const useBoardMove = (
  boardState: [EditorBoard, React.Dispatch<React.SetStateAction<EditorBoard>>],
  movable: boolean
): {
  isMoving: boolean;
  onMouseOut: () => void;
  onDragStart: (event: React.MouseEvent<SVGSVGElement>) => void;
  onDrag: (event: React.MouseEvent<SVGSVGElement>) => void;
  onDragEnd: () => void;
} => {
  const [board, setBoard] = boardState;

  const [isMoving, setIsMoving] = useState(false);
  const [dragOffset, setDragOffset] = useState<Coordinate | null>(null);

  const onMouseOut = () => {
    setIsMoving(false);
  };

  const onDragStart = (event: React.MouseEvent<SVGSVGElement>) => {
    if (!movable) return;

    const dragOffsetX = event.nativeEvent.offsetX - board.x;
    const dragOffsetY = event.nativeEvent.offsetY - board.y;

    setIsMoving(true);
    setDragOffset({ x: dragOffsetX, y: dragOffsetY });
  };

  const onDrag = (event: React.MouseEvent<SVGSVGElement>) => {
    if (!movable || !isMoving || dragOffset === null) return;

    const { offsetX, offsetY } = event.nativeEvent;

    setBoard((prevState) => ({
      ...prevState,
      x: offsetX - dragOffset.x,
      y: offsetY - dragOffset.y,
    }));
  };

  const onDragEnd = () => {
    if (!movable) return;

    setIsMoving(false);
    setDragOffset(null);
  };

  return {
    isMoving,
    onMouseOut,
    onDragStart,
    onDrag,
    onDragEnd,
  };
};

export default useBoardMove;
