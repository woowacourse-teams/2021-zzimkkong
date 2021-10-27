import React, { Dispatch, SetStateAction, useRef, useState } from 'react';
import { MapElement } from 'types/common';
import useBoardDragSelect from './useBoardDragSelect';

interface Props {
  mapElements: MapElement[];
  boardRef: React.RefObject<SVGSVGElement>;
}

const useBoardSelect = ({
  mapElements,
  boardRef,
}: Props): {
  dragSelectRect: typeof dragSelectRect;
  selectedMapElements: MapElement[];
  selectedGroupBBox: DOMRect | null;
  selectRectRef: React.RefObject<SVGRectElement>;
  selectedMapElementsGroupRef: React.RefObject<SVGGElement>;
  deselectMapElements: () => void;
  setSelectedMapElements: Dispatch<SetStateAction<MapElement[]>>;
  onSelectDragStart: (event: React.MouseEvent<SVGSVGElement>) => void;
  onSelectDrag: (event: React.MouseEvent<SVGSVGElement>) => void;
  onSelectDragEnd: () => void;
} => {
  const selectRectRef = useRef<SVGRectElement>(null);

  const [selectedMapElements, setSelectedMapElements] = useState<MapElement[]>([]);

  const deselectMapElements = () => {
    setSelectedMapElements([]);
  };

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
    deselectMapElements,
  });

  return {
    dragSelectRect,
    selectedMapElements,
    selectedGroupBBox,
    selectRectRef,
    selectedMapElementsGroupRef,
    deselectMapElements,
    setSelectedMapElements,
    onSelectDragStart,
    onSelectDrag,
    onSelectDragEnd,
  };
};

export default useBoardSelect;
