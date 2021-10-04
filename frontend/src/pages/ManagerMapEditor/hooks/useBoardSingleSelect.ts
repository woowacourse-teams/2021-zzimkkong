import React from 'react';
import { GripPoint, MapElement } from 'types/common';
import { MapElementType } from 'types/editor';

interface Props {
  nextGripPointId: number;
  setGripPoints: React.Dispatch<React.SetStateAction<GripPoint[]>>;
  setSelectedMapElements: React.Dispatch<React.SetStateAction<MapElement[]>>;
}

const useBoardSingleSelect = ({
  nextGripPointId,
  setGripPoints,
  setSelectedMapElements,
}: Props): {
  selectMapElement: (mapElement: MapElement) => void;
} => {
  const selectLineElement = (mapElement: MapElement) => {
    const points = mapElement.points.map((point) => {
      const [x, y] = point.split(',');

      return { x: Number(x), y: Number(y) };
    });

    const newGripPoints = points.map(
      (point, index): GripPoint => ({
        id: nextGripPointId + index,
        mapElement,
        x: point.x,
        y: point.y,
      })
    );

    setSelectedMapElements([mapElement]);
    setGripPoints([...newGripPoints]);
  };

  const selectRectElement = (mapElement: MapElement) => {
    const { x, y, width, height } = mapElement;

    const pointX = Number(x);
    const pointY = Number(y);
    const widthValue = Number(width);
    const heightValue = Number(height);

    const points = [
      { x: pointX, y: pointY },
      { x: pointX + widthValue, y: pointY },
      { x: pointX, y: pointY + heightValue },
      { x: pointX + widthValue, y: pointY + heightValue },
    ];

    const newGripPoints = points.map(
      (point, index): GripPoint => ({
        id: nextGripPointId + index,
        mapElement,
        x: point.x,
        y: point.y,
      })
    );

    setSelectedMapElements([mapElement]);
    setGripPoints([...newGripPoints]);
  };

  const selectMapElement = (mapElement: MapElement) => {
    if (mapElement.type === MapElementType.Polyline) {
      selectLineElement(mapElement);

      return;
    }

    if (mapElement.type === MapElementType.Rect) {
      selectRectElement(mapElement);
    }
  };

  return {
    selectMapElement,
  };
};

export default useBoardSingleSelect;
