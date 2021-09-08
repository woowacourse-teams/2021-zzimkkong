import { Dispatch, SetStateAction, useState } from 'react';
import { MapElement } from 'types/common';

interface Props {
  mapElements: [MapElement[], Dispatch<SetStateAction<MapElement[]>>];
}

const useBoardEraserTool = ({
  mapElements: [, setMapElements],
}: Props): {
  erasingMapElementIds: MapElement['id'][];
  isErasing: boolean;
  eraseStart: () => void;
  eraseEnd: () => void;
  selectErasingElement: (id: MapElement['id']) => void;
} => {
  const [erasingMapElementIds, setErasingMapElementIds] = useState<MapElement['id'][]>([]);
  const [isErasing, setErasing] = useState(false);

  const eraseEnd = () => {
    setErasing(false);
    setMapElements((prevMapElements) =>
      prevMapElements.filter(({ id }) => !erasingMapElementIds.includes(id))
    );
    setErasingMapElementIds([]);
  };

  const eraseStart = () => {
    if (erasingMapElementIds.length > 0) {
      eraseEnd();

      return;
    }
    setErasing(true);
    setErasingMapElementIds([]);
  };

  const selectErasingElement = (id: MapElement['id']) => {
    if (!isErasing) return;

    setErasingMapElementIds((prevIds) => [...prevIds, id]);
  };

  return {
    erasingMapElementIds,
    isErasing,
    eraseStart,
    eraseEnd,
    selectErasingElement,
  };
};

export default useBoardEraserTool;
