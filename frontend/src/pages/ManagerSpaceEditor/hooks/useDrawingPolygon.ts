import { useState } from 'react';
import { Area, Coordinate } from 'types/common';
import { DrawingAreaShape } from 'types/editor';

const useDrawingPolygon = (
  coordinate: Coordinate
): {
  polygon: Area | null;
  isDrawingPolygon: boolean;
  startPoint: Coordinate | null;
  points: Coordinate[];
  startDrawingPolygon: () => void;
  updatePolygon: () => void;
  endDrawingPolygon: () => void;
} => {
  const [polygon, setPolygon] = useState<Area | null>(null);
  const [isDrawingPolygon, setIsDrawingPolygon] = useState(false);
  const [points, setPoints] = useState<Coordinate[]>([]);
  const [startPoint, setStartPoint] = useState<Coordinate | null>(null);

  const startDrawingPolygon = () => {
    setPoints([coordinate]);
    setStartPoint(coordinate);
    setIsDrawingPolygon(true);
  };

  const updatePolygon = () => {
    setPoints([...points, coordinate]);
    setPolygon({
      shape: DrawingAreaShape.Polygon,
      points: [...points, coordinate],
    });
  };

  const endDrawingPolygon = () => {
    setPoints([]);
    setStartPoint(null);
    setIsDrawingPolygon(false);
  };

  return {
    polygon,
    isDrawingPolygon,
    startPoint,
    points,
    startDrawingPolygon,
    updatePolygon,
    endDrawingPolygon,
  };
};

export default useDrawingPolygon;
