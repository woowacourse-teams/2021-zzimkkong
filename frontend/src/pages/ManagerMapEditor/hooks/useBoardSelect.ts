import { useState } from 'react';
import { Coordinate, GripPoint, MapElement } from 'types/common';

const useBoardSelect = (): {
  gripPoints: GripPoint[];
  selectedMapElementId: number | null;
  selectLineElement: (target: SVGPolylineElement, id: MapElement['id']) => void;
  selectRectElement: (target: SVGRectElement, id: MapElement['id']) => void;
  deselectMapElement: () => void;
} => {
  const [gripPoints, setGripPoints] = useState<GripPoint[]>([]);
  const [selectedMapElementId, setSelectedMapElementId] = useState<MapElement['id'] | null>(null);
  const nextGripPointId = Math.max(...gripPoints.map(({ id }) => id), 1) + 1;

  const selectLineElement = (target: SVGPolylineElement, id: MapElement['id']) => {
    const points = Object.values<Coordinate>(target?.points).map(({ x, y }) => ({ x, y }));

    const newGripPoints = points.map(
      (point, index): GripPoint => ({
        id: nextGripPointId + index,
        mapElementId: id,
        x: point.x,
        y: point.y,
      })
    );

    setSelectedMapElementId(id);
    setGripPoints([...newGripPoints]);
  };

  const selectRectElement = (target: SVGRectElement, id: MapElement['id']) => {
    const { x, y, width, height } = target;

    const pointX = x.baseVal.value;
    const pointY = y.baseVal.value;
    const widthValue = width.baseVal.value;
    const heightValue = height.baseVal.value;

    const points = [
      { x: pointX, y: pointY },
      { x: pointX + widthValue, y: pointY },
      { x: pointX, y: pointY + heightValue },
      { x: pointX + widthValue, y: pointY + heightValue },
    ];

    const newGripPoints = points.map(
      (point, index): GripPoint => ({
        id: nextGripPointId + index,
        mapElementId: id,
        x: point.x,
        y: point.y,
      })
    );

    setSelectedMapElementId(id);
    setGripPoints([...newGripPoints]);
  };

  const deselectMapElement = () => {
    setSelectedMapElementId(null);
    setGripPoints([]);
  };

  return {
    gripPoints,
    selectedMapElementId,
    selectLineElement,
    selectRectElement,
    deselectMapElement,
  };
};

export default useBoardSelect;
