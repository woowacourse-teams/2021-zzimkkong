import { createContext } from 'react';

interface DrawerContextValue {
  open: boolean;
  onClose: () => void;
}

const DrawerContext = createContext<DrawerContextValue>({
  open: false,
  onClose: () => undefined,
});

if (process.env.NODE_ENV !== 'production') {
  DrawerContext.displayName = 'DrawerContext';
}

export default DrawerContext;
