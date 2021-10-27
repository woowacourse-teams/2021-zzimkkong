import { Dispatch, SetStateAction, useState } from 'react';
import { Coordinate, MapElement } from 'types/common';
import { MapElementType } from 'types/editor';

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
  const [moveStartCoordinate, setMoveStartCoordinate] = useState<Coordinate>({ x: 0, y: 0 });

  const onMoveStartElement = () => {
    setElementMoving(true);
    setOffset({ x: 0, y: 0 });
    setMoveStartCoordinate(coordinate);
  };

  const onMoveElement = () => {
    if (!isElementMoving) return;

    setOffset({
      x: coordinate.x - moveStartCoordinate.x,
      y: coordinate.y - moveStartCoordinate.y,
    });
  };

  const onMoveEndElement = () => {
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

        if (element.type === MapElementType.Rect && element.x && element.y) {
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

        if (element.type === MapElementType.Rect && element.x && element.y) {
          return {
            ...element,
            x: element.x + offset.x,
            y: element.y + offset.y,
          };
        }

        return element;
      })
    );
    setElementMoving(false);
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
