import { useState } from 'react';
import { SpaceEditorMode } from '../constants';

const useSpaceEditorMode = (
  initialMode?: SpaceEditorMode
): [SpaceEditorMode, (nextMode: SpaceEditorMode) => void] => {
  const [mode, setMode] = useState<SpaceEditorMode>(initialMode ?? SpaceEditorMode.Default);

  const setModeState = (nextMode: SpaceEditorMode) => {
    if (nextMode === SpaceEditorMode.Default && mode === SpaceEditorMode.Form) {
      setMode(SpaceEditorMode.Form);
    }

    setMode(nextMode);
  };

  return [mode, setModeState];
};

export default useSpaceEditorMode;
