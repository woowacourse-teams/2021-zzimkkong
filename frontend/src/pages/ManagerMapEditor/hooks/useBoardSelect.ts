import React, { useState } from 'react';
import { GripPoint, MapElement } from 'types/common';
import useBoardDragSelect from './useBoardDragSelect';
import useBoardSingleSelect from './useBoardSingleSelect';

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
  dragSelectRect: typeof dragSelectRect;
  gripPoints: GripPoint[];
  selectedMapElements: MapElement[];
  selectMapElement: (mapElement: MapElement) => void;
  deselectMapElements: () => void;
  onSelectDragStart: (event: React.MouseEvent<SVGSVGElement>) => void;
  onSelectDrag: (event: React.MouseEvent<SVGSVGElement>) => void;
  onSelectDragEnd: () => void;
} => {
  const [gripPoints, setGripPoints] = useState<GripPoint[]>([]);
  const [selectedMapElements, setSelectedMapElements] = useState<MapElement[]>([]);
  const nextGripPointId = Math.max(...gripPoints.map(({ id }) => id), 1) + 1;

  const deselectMapElements = () => {
    setSelectedMapElements([]);
    setGripPoints([]);
  };

  const { selectMapElement } = useBoardSingleSelect({
    nextGripPointId,
    setGripPoints,
    setSelectedMapElements,
  });

  const { dragSelectRect, onSelectDragStart, onSelectDrag, onSelectDragEnd } = useBoardDragSelect({
    mapElements,
    boardRef,
    selectRectRef,
    setSelectedMapElements,
    deselectMapElements,
  });

  return {
    dragSelectRect,
    gripPoints,
    selectedMapElements,
    deselectMapElements,
    selectMapElement,
    onSelectDragStart,
    onSelectDrag,
    onSelectDragEnd,
  };
};

export default useBoardSelect;
