import { Coordinate } from 'types/common';
export const getPolygonCenterPoint = (coordinates: Coordinate[]): Coordinate => {
  const x = coordinates.map((p) => p.x);
  const y = coordinates.map((p) => p.y);

  const maxX = Math.max(...x);
  const maxY = Math.max(...y);
  const minX = Math.min(...x);
  const minY = Math.min(...y);

  return {
    x: (minX + maxX) / 2,
    y: (minY + maxY) / 2,
  };
};
