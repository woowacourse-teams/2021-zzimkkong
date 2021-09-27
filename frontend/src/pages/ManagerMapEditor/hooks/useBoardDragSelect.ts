import React, { useState } from 'react';
import { Coordinate, MapElement } from 'types/common';

interface Props {
  mapElements: MapElement[];
  boardRef: React.RefObject<SVGSVGElement>;
  selectRectRef: React.RefObject<SVGRectElement>;
  setSelectedMapElements: React.Dispatch<React.SetStateAction<MapElement[]>>;
  deselectMapElements: () => void;
}

const useBoardDragSelect = ({
  mapElements,
  boardRef,
  selectRectRef,
  setSelectedMapElements,
  deselectMapElements,
}: Props): {
  dragSelectRect: typeof dragSelectRect;
  onSelectDragStart: (event: React.MouseEvent<SVGSVGElement>) => void;
  onSelectDrag: (event: React.MouseEvent<SVGSVGElement>) => void;
  onSelectDragEnd: () => void;
} => {
  const [isSelectDragging, setSelectDragging] = useState(false);
  const [startCoordinate, setStartCoordinate] = useState<Coordinate>({ x: 0, y: 0 });
  const [dragSelectRect, setDragSelectRect] = useState({
    x: 0,
    y: 0,
    width: 0,
    height: 0,
  });

  const getSelections = () => {
    if (!selectRectRef.current) return [];

    const selections: MapElement[] = [];
    const selectRectBBox = selectRectRef.current.getBBox();

    mapElements.forEach((element) => {
      if (element.ref.current) {
        const hasIntersection = boardRef.current?.checkIntersection(
          element.ref?.current,
          selectRectBBox
        );

        if (hasIntersection) {
          selections.push(element);
        }
      }
    });

    return selections;
  };

  const selectDragEnd = () => {
    const selections = getSelections();
    setSelectedMapElements(selections);

    setSelectDragging(false);
    setStartCoordinate({ x: 0, y: 0 });
    setDragSelectRect({
      x: 0,
      y: 0,
      width: 0,
      height: 0,
    });
  };

  const onSelectDragStart = (event: React.MouseEvent<SVGSVGElement>) => {
    const { offsetX, offsetY } = event.nativeEvent;

    if (isSelectDragging) {
      selectDragEnd();
      return;
    }

    deselectMapElements();

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
    if (!isSelectDragging) return;

    const { offsetX, offsetY } = event.nativeEvent;

    setDragSelectRect({
      x: Math.min(startCoordinate.x, offsetX),
      y: Math.min(startCoordinate.y, offsetY),
      width: Math.abs(startCoordinate.x - offsetX),
      height: Math.abs(startCoordinate.y - offsetY),
    });
  };

  const onSelectDragEnd = () => {
    if (!isSelectDragging) return;

    selectDragEnd();
  };

  return {
    dragSelectRect,
    onSelectDragStart,
    onSelectDrag,
    onSelectDragEnd,
  };
};

export default useBoardDragSelect;
