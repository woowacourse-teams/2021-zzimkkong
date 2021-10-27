import React, { Dispatch, SetStateAction, useCallback, useEffect, useRef, useState } from 'react';
import { Coordinate, MapElement } from 'types/common';

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
  const selectedMapElementsGroupRef = useRef<SVGGElement>(null);

  const [selectedMapElements, setSelectedMapElements] = useState<MapElement[]>([]);

  const deselectMapElements = () => {
    setSelectedMapElements([]);
  };

  const [isSelectDragging, setSelectDragging] = useState(false);
  const [startCoordinate, setStartCoordinate] = useState<Coordinate>({ x: 0, y: 0 });
  const [dragSelectRect, setDragSelectRect] = useState({
    x: 0,
    y: 0,
    width: 0,
    height: 0,
  });
  const [selectedGroupBBox, setSelectedGroupBBox] = useState<DOMRect | null>(null);

  const getSelections = () => {
    if (!selectRectRef.current) return [];

    const selections: MapElement[] = [];
    const selectRectBBox = selectRectRef.current.getBBox();

    mapElements.forEach((element) => {
      if (element.ref.current) {
        const hasEnclosure = boardRef.current?.checkEnclosure(element.ref?.current, selectRectBBox);

        if (hasEnclosure) {
          selections.push(element);
        }
      }
    });

    return selections;
  };

  const selectDragEnd = () => {
    deselectMapElements();

    setSelectDragging(false);
    setStartCoordinate({ x: 0, y: 0 });
    setDragSelectRect({
      x: 0,
      y: 0,
      width: 0,
      height: 0,
    });

    const selections = getSelections();
    if (!selections.length) return;

    setSelectedMapElements(selections);
  };

  const onSelectDragStart = (event: React.MouseEvent<SVGSVGElement>) => {
    const { offsetX, offsetY } = event.nativeEvent;

    if (isSelectDragging) {
      selectDragEnd();

      return;
    }

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

  const setGroupBBox = useCallback(() => {
    const bBox = selectedMapElementsGroupRef.current?.getBBox() ?? null;
    const positionOffset = 2;
    const marginOffset = positionOffset * 2;

    if (!bBox || !selectedMapElements.length) {
      setSelectedGroupBBox(null);

      return;
    }

    const newBBox = {
      ...bBox,
      x: bBox.x - positionOffset,
      y: bBox.y - positionOffset,
      width: bBox.width + marginOffset,
      height: bBox.height + marginOffset,
    };

    setSelectedGroupBBox(newBBox);
  }, [selectedMapElements]);

  useEffect(() => {
    setGroupBBox();
  }, [setGroupBBox]);

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
