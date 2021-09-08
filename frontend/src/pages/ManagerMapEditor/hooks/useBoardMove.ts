import { useState } from 'react';
import { EditorBoard } from 'types/common';

const useBoardMove = (
  statusState: [EditorBoard, React.Dispatch<React.SetStateAction<EditorBoard>>],
  isDraggable: boolean
): {
  isDragging: boolean;
  onDragStart: (event: React.MouseEvent<SVGElement>) => void;
  onDrag: (event: React.MouseEvent<SVGElement>) => void;
  onDragEnd: () => void;
  onMouseOut: () => void;
} => {
  const [status, setStatus] = statusState;

  const [isDragging, setDragging] = useState(false);
  const [dragOffset, setDragOffset] = useState({ x: 0, y: 0 });

  const handleDragStart = (event: React.MouseEvent<SVGElement>) => {
    if (!isDraggable) return;

    setDragOffset({
      x: event.nativeEvent.offsetX - status.x,
      y: event.nativeEvent.offsetY - status.y,
    });

    setDragging(true);
  };

  const handleDrag = (event: React.MouseEvent<SVGElement>) => {
    if (!isDraggable || !isDragging) return;

    const { offsetX, offsetY } = event.nativeEvent;

    setStatus((prevState) => ({
      ...prevState,
      x: offsetX - dragOffset.x,
      y: offsetY - dragOffset.y,
    }));
  };

  const handleDragEnd = () => {
    if (!isDraggable) return;

    setDragOffset({ x: 0, y: 0 });
    setDragging(false);
  };

  const handleMouseOut = () => {
    setDragging(false);
  };

  return {
    isDragging,
    onDragStart: handleDragStart,
    onDrag: handleDrag,
    onDragEnd: handleDragEnd,
    onMouseOut: handleMouseOut,
  };
};

export default useBoardMove;
