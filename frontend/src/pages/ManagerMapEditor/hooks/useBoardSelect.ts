import React, { useState } from 'react';
import { Coordinate, GripPoint, MapElement } from 'types/common';
import { MapElementType } from 'types/editor';

interface Props {
  mapElements: MapElement[];
  boardRef: React.RefObject<SVGSVGElement>;
  selectRectRef: React.RefObject<SVGRectElement>;
}

const useBoardSelect = ({
  mapElements,
  boardRef,
  selectRectRef,
}: Props): {
  isSelectDragging: boolean;
  dragSelectRect: typeof dragSelectRect;
  gripPoints: GripPoint[];
  selectedMapElements: MapElement[];
  deselectMapElement: () => void;
  onClickBoard: () => void;
  selectMapElement: (mapElement: MapElement) => void;
  onSelectDragStart: (event: React.MouseEvent<SVGSVGElement>) => void;
  onSelectDrag: (event: React.MouseEvent<SVGSVGElement>) => void;
  onSelectDragEnd: () => void;
} => {
  const [isSelectDragging, setSelectDragging] = useState(false);
  const [dragSelectRect, setDragSelectRect] = useState({
    x: 0,
    y: 0,
    width: 0,
    height: 0,
  });
  const [startCoordinate, setStartCoordinate] = useState<Coordinate>({ x: 0, y: 0 });
  const [gripPoints, setGripPoints] = useState<GripPoint[]>([]);
  const [selectedMapElements, setSelectedMapElements] = useState<MapElement[]>([]);
  const nextGripPointId = Math.max(...gripPoints.map(({ id }) => id), 1) + 1;

  const checkSelection = () => {
    if (!selectRectRef.current) return;

    const selections: MapElement[] = [];
    const selectRectBBox = selectRectRef.current.getBBox();
    console.log(selectRectBBox, selectRectRef.current);

    mapElements.forEach((element) => {
      if (element.ref.current) {
        const hasIntersection = boardRef.current?.checkIntersection(
          element.ref?.current,
          selectRectBBox
        );

        if (hasIntersection) {
          console.log(element.ref.current);
          selections.push(element);
        }
      }
    });

    setSelectedMapElements(selections);
  };

  const onSelectDragStart = (event: React.MouseEvent<SVGSVGElement>) => {
    const { offsetX, offsetY } = event.nativeEvent;

    setSelectDragging(true);
    setStartCoordinate({ x: offsetX, y: offsetY });
    setDragSelectRect({
      x: offsetX,
      y: offsetY,
      width: 0,
      height: 0,
    });
  };

  const onSelectDrag = (event: React.MouseEvent<SVGSVGElement>) => {
    const { offsetX, offsetY } = event.nativeEvent;

    setDragSelectRect({
      x: Math.min(startCoordinate.x, offsetX),
      y: Math.min(startCoordinate.y, offsetY),
      width: Math.abs(startCoordinate.x - offsetX),
      height: Math.abs(startCoordinate.y - offsetY),
    });
  };

  const onSelectDragEnd = () => {
    setSelectDragging(false);
    checkSelection();

    setStartCoordinate({ x: 0, y: 0 });
    setDragSelectRect({
      x: 0,
      y: 0,
      width: 0,
      height: 0,
    });
  };

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

  const deselectMapElement = () => {
    setSelectedMapElements([]);
    setGripPoints([]);
  };

  const onClickBoard = () => {
    deselectMapElement();
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
    isSelectDragging,
    dragSelectRect,
    gripPoints,
    selectedMapElements,
    deselectMapElement,
    onClickBoard,
    selectMapElement,
    onSelectDragStart,
    onSelectDrag,
    onSelectDragEnd,
  };
};

export default useBoardSelect;
