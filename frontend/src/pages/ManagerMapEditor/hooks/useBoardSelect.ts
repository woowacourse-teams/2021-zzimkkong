import React, { useRef, useState } from 'react';
import { GripPoint, MapElement } from 'types/common';
import useBoardDragSelect from './useBoardDragSelect';
import useBoardSingleSelect from './useBoardSingleSelect';

interface Props {
  mapElements: MapElement[];
  boardRef: React.RefObject<SVGSVGElement>;
}

const useBoardSelect = ({
  mapElements,
  boardRef,
}: Props): {
  dragSelectRect: typeof dragSelectRect;
  gripPoints: GripPoint[];
  selectedMapElements: MapElement[];
  selectedGroupBBox: DOMRect | null;
  selectRectRef: React.RefObject<SVGRectElement>;
  selectedMapElementsGroupRef: React.RefObject<SVGGElement>;
  selectMapElement: (mapElement: MapElement) => void;
  deselectMapElements: () => void;
  onSelectDragStart: (event: React.MouseEvent<SVGSVGElement>) => void;
  onSelectDrag: (event: React.MouseEvent<SVGSVGElement>) => void;
  onSelectDragEnd: () => void;
} => {
  const selectRectRef = useRef<SVGRectElement>(null);

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

  const {
    dragSelectRect,
    selectedGroupBBox,
    selectedMapElementsGroupRef,
    onSelectDragStart,
    onSelectDrag,
    onSelectDragEnd,
  } = useBoardDragSelect({
    mapElements,
    boardRef,
    selectRectRef,
    selectedMapElementsState: [selectedMapElements, setSelectedMapElements],
    selectSingleMapElement: selectMapElement,
    deselectMapElements,
  });

  return {
    dragSelectRect,
    gripPoints,
    selectedMapElements,
    selectedGroupBBox,
    selectRectRef,
    selectedMapElementsGroupRef,
    deselectMapElements,
    selectMapElement,
    onSelectDragStart,
    onSelectDrag,
    onSelectDragEnd,
  };
};

export default useBoardSelect;
