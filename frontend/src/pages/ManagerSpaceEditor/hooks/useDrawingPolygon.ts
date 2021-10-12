import { useEffect, useState } from 'react';
import { Area, Coordinate } from 'types/common';
import { DrawingAreaShape, SpaceEditorMode as Mode } from 'types/editor';

interface Props {
  coordinate: Coordinate;
  mode: Mode;
}

const useDrawingPolygon = ({
  coordinate,
  mode,
}: Props): {
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
    setPolygon(null);
    setStartPoint(null);
    setIsDrawingPolygon(false);
  };

  useEffect(() => {
    if (mode !== Mode.Polygon) {
      endDrawingPolygon();
    }
  }, [mode]);

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
