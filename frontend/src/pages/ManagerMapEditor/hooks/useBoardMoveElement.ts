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
  const [isElementMoving, setElementMoving] = useState(false);
  const [offset, setOffset] = useState<Coordinate>({ x: 0, y: 0 });
  const [initialCoordinate, setInitialCoordinate] = useState<Coordinate>({ x: 0, y: 0 });

  const onMoveStartElement = () => {
    setElementMoving(true);
    setOffset({ x: 0, y: 0 });
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
    setElementMoving(false);
    setOffset({ x: 0, y: 0 });

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
            x: Number(element.x) + offset.x,
            y: Number(element.y) + offset.y,
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
            x: Number(element.x) + offset.x,
            y: Number(element.y) + offset.y,
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
