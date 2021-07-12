import { createContext } from 'react';
import { Props as PanelProps } from './Panel';

const PanelContext = createContext<PanelProps>({
  expandable: true,
  expanded: false,
});

if (process.env.NODE_ENV !== 'production') {
  PanelContext.displayName = 'PanelContext';
}

export default PanelContext;
