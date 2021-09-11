import { useState } from 'react';
import { SpaceEditorMode as Mode } from '../constants';

const useSpaceEditorMode = (): [Mode, (nextMode: Mode) => void] => {
  const [mode, setMode] = useState<Mode>(Mode.Default);

  const setModeState = (nextMode: Mode) => {
    if (nextMode === Mode.Default && mode === Mode.Form) {
      setMode(Mode.Form);
    }

    setMode(nextMode);
  };

  return [mode, setModeState];
};

export default useSpaceEditorMode;
