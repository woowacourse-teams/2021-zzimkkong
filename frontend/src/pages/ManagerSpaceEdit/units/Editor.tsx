import { Dispatch, SetStateAction, useEffect, useState } from 'react';
import { EDITOR, KEY } from 'constants/editor';
import { EditorBoard, ManagerSpace, MapElement } from 'types/common';
import { SpaceEditorMode as Mode } from '../constants';
import useBindKeyPress from '../hooks/useBindKeyPress';
import useBoardCoordinate from '../hooks/useBoardCoordinate';
import useFormContext from '../hooks/useFormContext';
import { SpaceFormContext } from '../providers/SpaceFormProvider';
import Board from './Board';

interface Props {
  mode: Mode;
  boardState: [EditorBoard, Dispatch<SetStateAction<EditorBoard>>];
  selectedSpaceIdState: [number | null, Dispatch<SetStateAction<number | null>>];
  mapElements: MapElement[];
  spaces: ManagerSpace[];
}

const Editor = ({
  mode,
  boardState,
  selectedSpaceIdState,
  mapElements,
  spaces,
}: Props): JSX.Element => {
  const [board] = boardState;
  const [selectedSpaceId, setSelectedSpaceId] = selectedSpaceIdState;

  const { pressedKey } = useBindKeyPress();
  const [movable, setMovable] = useState(pressedKey === KEY.SPACE);

  const { values } = useFormContext(SpaceFormContext);
  const { coordinate, stickyCoordinate, onMouseMove } = useBoardCoordinate(board);

  const isDrawing = mode === Mode.Rect && !movable;

  const handleClickSpace = (id: number) => {
    if (isDrawing) return;

    setSelectedSpaceId(id);
  };

  useEffect(() => {
    setMovable(pressedKey === KEY.SPACE);
  }, [movable, pressedKey]);

  return (
    <Board movable={movable} boardState={boardState} onMouseMove={onMouseMove}>
      {isDrawing && <Board.CursorRect coordinate={stickyCoordinate} size={EDITOR.GRID_SIZE} />}

      {spaces?.map((space, index) => (
        <Board.Space
          key={`space-${index}`}
          space={
            space.id === selectedSpaceId
              ? { ...space, name: values.name, color: values.color }
              : space
          }
          drawing={isDrawing}
          selected={space.id === selectedSpaceId}
          onClick={() => handleClickSpace(space.id)}
        />
      ))}

      {mapElements?.map((element, index) => (
        <Board.MapElement key={`map-${index}`} mapElement={element} />
      ))}
    </Board>
  );
};

export default Editor;
