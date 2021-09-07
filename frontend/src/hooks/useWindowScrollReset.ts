import { useEffect } from 'react';

const useWindowScrollReset = (): void => {
  useEffect(() => {
    window.scrollTo(0, 0);
  }, []);
};

export default useWindowScrollReset;
