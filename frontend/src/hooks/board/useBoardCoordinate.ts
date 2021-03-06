import React, { useState } from 'react';
import { EDITOR } from 'constants/editor';
import { Coordinate, EditorBoard } from 'types/common';

const useBoardCoordinate = (
  boardStatus: EditorBoard
): {
  coordinate: Coordinate;
  stickyDotCoordinate: Coordinate;
  stickyRectCoordinate: Coordinate;
  onMouseMove: (event: React.MouseEvent<SVGSVGElement>) => void;
} => {
  const [coordinate, setCoordinate] = useState<Coordinate>({ x: 0, y: 0 });
  const stickyDotCoordinate: Coordinate = {
    x: Math.round(coordinate.x / EDITOR.GRID_SIZE) * EDITOR.GRID_SIZE,
    y: Math.round(coordinate.y / EDITOR.GRID_SIZE) * EDITOR.GRID_SIZE,
  };
  const stickyRectCoordinate: Coordinate = {
    x: Math.floor(coordinate.x / EDITOR.GRID_SIZE) * EDITOR.GRID_SIZE,
    y: Math.floor(coordinate.y / EDITOR.GRID_SIZE) * EDITOR.GRID_SIZE,
  };

  const getSVGCoordinate = (event: React.MouseEvent<SVGSVGElement>) => {
    const svg = (event.nativeEvent.target as SVGSVGElement)?.ownerSVGElement;
    if (!svg) return { x: -1, y: -1 };

    let point = svg.createSVGPoint();

    point.x = event.nativeEvent.clientX;
    point.y = event.nativeEvent.clientY;
    point = point.matrixTransform(svg.getScreenCTM()?.inverse());

    const x = (point.x - boardStatus.x) * (1 / boardStatus.scale);
    const y = (point.y - boardStatus.y) * (1 / boardStatus.scale);

    return { x, y };
  };

  const onMouseMove = (event: React.MouseEvent<SVGSVGElement>) => {
    const { x, y } = getSVGCoordinate(event);
    setCoordinate({ x, y });
  };

  return { coordinate, stickyDotCoordinate, stickyRectCoordinate, onMouseMove };
};

export default useBoardCoordinate;
