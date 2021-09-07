import { Dispatch, SetStateAction } from 'react';
import { Color, Coordinate, DrawingStatus, MapElement } from 'types/common';

interface Props {
  coordinate: Coordinate;
  color: Color;
  drawingStatus: [DrawingStatus, Dispatch<SetStateAction<DrawingStatus>>];
  mapElements: [MapElement[], Dispatch<SetStateAction<MapElement[]>>];
}

const useBoardRectTool = ({
  coordinate,
  color,
  drawingStatus: [drawingStatus, setDrawingStatus],
  mapElements: [mapElements, setMapElements],
}: Props): {
  drawRectStart: () => void;
  drawRectEnd: () => void;
} => {
  const nextMapElementId = Math.max(...mapElements.map(({ id }) => id), 1) + 1;

  const drawRectStart = () => {
    if (drawingStatus.start) {
      const startPoint = `${drawingStatus.start.x},${drawingStatus.start.y}`;
      const endPoint = `${coordinate.x},${coordinate.y}`;

      setMapElements((prevState) => [
        ...prevState,
        {
          id: nextMapElementId,
          type: 'rect',
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

  const drawRectEnd = () => {
    if (!drawingStatus || !drawingStatus.start) return;

    const startPoint = {
      x: drawingStatus.start.x,
      y: drawingStatus.start.y,
    };

    const endPoint = {
      x: coordinate.x,
      y: coordinate.y,
    };

    const width = Math.abs(startPoint.x - endPoint.x);
    const height = Math.abs(startPoint.y - endPoint.y);

    const startCoordinate = `${startPoint.x}, ${startPoint.y}`;
    const endCoordinate = `${endPoint.x}, ${endPoint.y}`;

    setDrawingStatus({});

    if (startCoordinate === endCoordinate) return;

    if (!width || !height) {
      setMapElements((prevState) => [
        ...prevState,
        {
          id: nextMapElementId,
          type: 'polyline',
          stroke: color,
          points: [`${startPoint.x},${startPoint.y}`, `${endPoint.x},${endPoint.y}`],
        },
      ]);

      return;
    }

    setMapElements((prevState) => [
      ...prevState,
      {
        id: nextMapElementId,
        type: 'rect',
        stroke: color,
        width,
        height,
        x: Math.min(startPoint.x, endPoint.x),
        y: Math.min(startPoint.y, endPoint.y),
        points: [startCoordinate, endCoordinate],
      },
    ]);
  };

  return {
    drawRectStart,
    drawRectEnd,
  };
};

export default useBoardRectTool;
