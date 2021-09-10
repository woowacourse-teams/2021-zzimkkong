import { useState } from 'react';
import { KEY } from 'constants/editor';
import { SpaceEditorMode as Mode } from '../constants';

const useSpaceEditorMode = (pressedKey: string): [Mode, (nextMode: Mode) => void] => {
  const [mode, setMode] = useState<Mode>(Mode.Default);

  const setModeState = (nextMode: Mode) => {
    if (nextMode === Mode.Default && mode === Mode.Form) {
      setMode(Mode.Form);
    }

    setMode(nextMode);
  };

  if (pressedKey === KEY.SPACE) {
    return [Mode.Move, setModeState];
  }

  return [mode, setModeState];
};

export default useSpaceEditorMode;
