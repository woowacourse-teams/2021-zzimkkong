import { useCallback, useEffect, useState } from 'react';

const useBindKeyPress = (): { pressedKey: string } => {
  const [pressedKey, setPressedKey] = useState('');

  const handleKeyDown = useCallback((event: KeyboardEvent) => {
    setPressedKey(event.key);
  }, []);

  const handleKeyUp = useCallback(() => {
    setPressedKey('');
  }, []);

  useEffect(() => {
    document.addEventListener('keydown', handleKeyDown);
    document.addEventListener('keyup', handleKeyUp);

    return () => {
      document.removeEventListener('keydown', handleKeyDown);
      document.removeEventListener('keyup', handleKeyUp);
    };
  }, [handleKeyDown, handleKeyUp]);

  return { pressedKey };
};

export default useBindKeyPress;
