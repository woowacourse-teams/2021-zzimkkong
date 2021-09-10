import { Dispatch, SetStateAction } from 'react';
import { Color, Coordinate, DrawingStatus, MapElement } from 'types/common';
import { MapElementType } from 'types/editor';

interface Props {
  coordinate: Coordinate;
  color: Color;
  drawingStatus: [DrawingStatus, Dispatch<SetStateAction<DrawingStatus>>];
  mapElements: [MapElement[], Dispatch<SetStateAction<MapElement[]>>];
}

const useBoardLineTool = ({
  coordinate,
  color,
  drawingStatus: [drawingStatus, setDrawingStatus],
  mapElements: [mapElements, setMapElements],
}: Props): {
  drawLineStart: () => void;
  drawLineEnd: () => void;
} => {
  const nextMapElementId = Math.max(...mapElements.map(({ id }) => id), 1) + 1;

  const drawLineStart = () => {
    if (drawingStatus.start) {
      const startPoint = `${drawingStatus.start.x},${drawingStatus.start.y}`;
      const endPoint = `${coordinate.x},${coordinate.y}`;

      setMapElements((prevState) => [
        ...prevState,
        {
          id: nextMapElementId,
          type: MapElementType.Polyline,
          stroke: color,
          points: [startPoint, endPoint],
        },
      ]);

      return;
    }

    setDrawingStatus((prevState) => ({
      ...prevState,
      start: coordinate,
    }));
  };

  const drawLineEnd = () => {
    if (!drawingStatus || !drawingStatus.start) return;

    const startPoint = `${drawingStatus.start.x},${drawingStatus.start.y}`;
    const endPoint = `${coordinate.x},${coordinate.y}`;

    setDrawingStatus({});

    if (startPoint === endPoint) return;

    setMapElements((prevState) => [
      ...prevState,
      {
        id: nextMapElementId,
        type: MapElementType.Polyline,
        stroke: color,
        points: [startPoint, endPoint],
      },
    ]);
  };

  return {
    drawLineStart,
    drawLineEnd,
  };
};

export default useBoardLineTool;
