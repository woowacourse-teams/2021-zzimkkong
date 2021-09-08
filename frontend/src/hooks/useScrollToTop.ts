import { useEffect } from 'react';

const useScrollToTop = (): void => {
  useEffect(() => {
    window.scrollTo(0, 0);
  }, []);
};

export default useScrollToTop;
