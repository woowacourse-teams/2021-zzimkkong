import { Dispatch, SetStateAction, useState } from 'react';
import { Coordinate, MapElement } from 'types/common';
import { MapElementType } from 'types/editor';
import { isNullish } from 'utils/type';

interface Props {
  coordinate: Coordinate;
  selectedMapElements: MapElement[];
  setMapElements: Dispatch<SetStateAction<MapElement[]>>;
  setSelectedMapElements: Dispatch<SetStateAction<MapElement[]>>;
}

const useBoardMoveElement = ({
  coordinate,
  selectedMapElements,
  setMapElements,
  setSelectedMapElements,
}: Props): {
  isElementMoving: boolean;
  offset: Coordinate;
  onMoveStartElement: () => void;
  onMoveElement: () => void;
  onMoveEndElement: () => void;
} => {
  const initialOffset = { x: 0, y: 0 };

  const [offset, setOffset] = useState<Coordinate>(initialOffset);
  const [initialCoordinate, setInitialCoordinate] = useState<Coordinate | null>(null);
  const isElementMoving = initialCoordinate !== null;

  const onMoveStartElement = () => {
    setOffset(initialOffset);
    setInitialCoordinate(coordinate);
  };

  const onMoveElement = () => {
    if (!isElementMoving) return;

    setOffset({
      x: coordinate.x - initialCoordinate.x,
      y: coordinate.y - initialCoordinate.y,
    });
  };

  const onMoveEndElement = () => {
    setOffset(initialOffset);
    setInitialCoordinate(null);

    setSelectedMapElements((prevElements) =>
      prevElements.map((element) => {
        if (element.type === MapElementType.Polyline && element.points) {
          const newPoints = element.points.map((point) => {
            const [x, y] = point.split(',').map((value) => Number(value));

            return `${x + offset.x},${y + offset.y}`;
          });

          return {
            ...element,
            points: newPoints,
          };
        }

        if (element.type === MapElementType.Rect) {
          if (isNullish(element.x) || isNullish(element.y)) {
            return element;
          }

          return {
            ...element,
            x: element.x + offset.x,
            y: element.y + offset.y,
          };
        }

        return element;
      })
    );

    setMapElements((prevElements) =>
      prevElements.map((element) => {
        if (!selectedMapElements?.find(({ id }) => id === element.id)) {
          return element;
        }

        if (element.type === MapElementType.Polyline && element.points) {
          const newPoints = element.points.map((point) => {
            const [x, y] = point.split(',').map((value) => Number(value));

            return `${x + offset.x},${y + offset.y}`;
          });

          return {
            ...element,
            points: newPoints,
          };
        }

        if (element.type === MapElementType.Rect) {
          if (isNullish(element.x) || isNullish(element.y)) {
            return element;
          }

          return {
            ...element,
            x: element.x + offset.x,
            y: element.y + offset.y,
          };
        }

        return element;
      })
    );
  };

  return {
    isElementMoving,
    offset,
    onMoveStartElement,
    onMoveElement,
    onMoveEndElement,
  };
};

export default useBoardMoveElement;
