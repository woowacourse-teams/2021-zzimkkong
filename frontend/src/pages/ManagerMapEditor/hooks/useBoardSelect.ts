import React, { useState } from 'react';
import { Coordinate, GripPoint, MapElement } from 'types/common';

interface Props {
  coordinate: Coordinate;
}

const useBoardSelect = ({
  coordinate,
}: Props): {
  isSelectDragging: boolean;
  dragSelectRect: typeof dragSelectRect;
  gripPoints: GripPoint[];
  selectedMapElementId: number | null;
  deselectMapElement: () => void;
  onClickBoard: () => void;
  onClickMapElement: (event: React.MouseEvent<SVGPolylineElement | SVGRectElement>) => void;
  onSelectDragStart: () => void;
  onSelectDragEnd: () => void;
} => {
  const [isSelectDragging, setSelectDragging] = useState(false);
  const [dragSelectRect, setDragSelectRect] = useState({
    x: 0,
    y: 0,
    width: 0,
    height: 0,
  });
  const [gripPoints, setGripPoints] = useState<GripPoint[]>([]);
  const [selectedMapElementId, setSelectedMapElementId] = useState<MapElement['id'] | null>(null);
  const nextGripPointId = Math.max(...gripPoints.map(({ id }) => id), 1) + 1;

  const onSelectDragStart = () => {
    setSelectDragging(true);
    setDragSelectRect({
      x: coordinate.x,
      y: coordinate.y,
      width: 0,
      height: 0,
    });
  };

  const onSelectDragEnd = () => {
    setSelectDragging(false);
    setDragSelectRect({
      x: 0,
      y: 0,
      width: 0,
      height: 0,
    });
  };

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

  const onClickBoard = () => {
    deselectMapElement();
  };

  const onClickMapElement = (event: React.MouseEvent<SVGPolylineElement | SVGRectElement>) => {
    const target = event.target as SVGElement;
    const [mapElementType, mapElementId] = target.id.split('-');

    if (mapElementType === 'polyline') {
      selectLineElement(event.target as SVGPolylineElement, Number(mapElementId));

      return;
    }

    if (mapElementType === 'rect') {
      selectRectElement(event.target as SVGRectElement, Number(mapElementId));
    }
  };

  return {
    isSelectDragging,
    dragSelectRect,
    gripPoints,
    selectedMapElementId,
    deselectMapElement,
    onClickBoard,
    onClickMapElement,
    onSelectDragStart,
    onSelectDragEnd,
  };
};

export default useBoardSelect;
