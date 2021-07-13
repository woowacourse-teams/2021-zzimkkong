import { createContext } from 'react';

interface PanelContextValue {
  expandable: boolean;
  expanded: boolean;
  onToggle: () => void;
}

const PanelContext = createContext<PanelContextValue>({
  expandable: true,
  expanded: false,
  onToggle: () => {},
});

if (process.env.NODE_ENV !== 'production') {
  PanelContext.displayName = 'PanelContext';
}

export default PanelContext;
