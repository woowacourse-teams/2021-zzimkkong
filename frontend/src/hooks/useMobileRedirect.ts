import { useState, useEffect } from 'react';
import { useHistory } from 'react-router';
import PATH from 'constants/path';

const getWindowWidth = () => {
  const { innerWidth } = window;

  return innerWidth;
};

const useMobileRedirect = (): void => {
  const history = useHistory();

  const [windowWidth, setWindowWidth] = useState(getWindowWidth());

  const handleResize = () => {
    setWindowWidth(getWindowWidth());
  };

  useEffect(() => {
    if (windowWidth < 900) {
      history.push(PATH.MOBILE_REDIRECT);
    }

    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, [history, windowWidth]);
};

export default useMobileRedirect;
