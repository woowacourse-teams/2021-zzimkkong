import { useState } from 'react';
import { EDITOR } from 'constants/editor';
import { Area, Coordinate } from 'types/common';
import { DrawingAreaShape } from 'types/editor';

const useDrawingRect = (
  coordinate: Coordinate
): {
  rect: Area | null;
  startDrawingRect: () => void;
  updateRect: () => void;
  endDrawingRect: () => void;
} => {
  const [drawingStartCoordinate, setDrawingStartCoordinate] = useState<Coordinate | null>(null);
  const [rect, setRect] = useState<Area | null>(null);

  const getRect = (): Area | null => {
    if (!drawingStartCoordinate) return null;

    const [startX, endX] =
      drawingStartCoordinate.x > coordinate.x
        ? [coordinate.x, drawingStartCoordinate.x]
        : [drawingStartCoordinate.x, coordinate.x];
    const [startY, endY] =
      drawingStartCoordinate.y > coordinate.y
        ? [coordinate.y, drawingStartCoordinate.y]
        : [drawingStartCoordinate.y, coordinate.y];

    const width = Math.abs(startX - endX) + EDITOR.GRID_SIZE;
    const height = Math.abs(startY - endY) + EDITOR.GRID_SIZE;

    return {
      shape: DrawingAreaShape.Rect,
      x: startX,
      y: startY,
      width,
      height,
    };
  };

  const startDrawingRect = () => {
    setDrawingStartCoordinate(coordinate);
  };

  const updateRect = () => {
    setRect(getRect());
  };

  const endDrawingRect = () => {
    setDrawingStartCoordinate(null);
    setRect(null);
  };

  return { rect, startDrawingRect, updateRect, endDrawingRect };
};

export default useDrawingRect;
