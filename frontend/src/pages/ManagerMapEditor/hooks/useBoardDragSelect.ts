import React, { useCallback, useEffect, useRef, useState } from 'react';
import { Coordinate, MapElement } from 'types/common';

interface Props {
  mapElements: MapElement[];
  boardRef: React.RefObject<SVGSVGElement>;
  selectRectRef: React.RefObject<SVGRectElement>;
  selectedMapElementsState: [MapElement[], React.Dispatch<React.SetStateAction<MapElement[]>>];
  selectSingleMapElement: (mapElement: MapElement) => void;
  deselectMapElements: () => void;
}

const useBoardDragSelect = ({
  mapElements,
  boardRef,
  selectRectRef,
  selectedMapElementsState,
  selectSingleMapElement,
  deselectMapElements,
}: Props): {
  dragSelectRect: typeof dragSelectRect;
  selectedGroupBBox: DOMRect | null;
  selectedMapElementsGroupRef: React.RefObject<SVGGElement>;
  onSelectDragStart: (event: React.MouseEvent<SVGSVGElement>) => void;
  onSelectDrag: (event: React.MouseEvent<SVGSVGElement>) => void;
  onSelectDragEnd: () => void;
} => {
  const [selectedMapElements, setSelectedMapElements] = selectedMapElementsState;

  const selectedMapElementsGroupRef = useRef<SVGGElement>(null);

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

    if (selections.length === 1) {
      const [mapElement] = selections;
      selectSingleMapElement(mapElement);

      return;
    }

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

    if (!bBox?.width || !bBox?.height) {
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
  }, []);

  useEffect(() => {
    if (selectedMapElements.length === 1) return;

    setGroupBBox();
  }, [setGroupBBox, selectedMapElements]);

  return {
    dragSelectRect,
    selectedGroupBBox,
    selectedMapElementsGroupRef,
    onSelectDragStart,
    onSelectDrag,
    onSelectDragEnd,
  };
};

export default useBoardDragSelect;
