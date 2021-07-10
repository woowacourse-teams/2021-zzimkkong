import { PropsWithChildren } from 'react';
import * as Styled from './Panel.styles';
import PanelContext from './PanelContext';
import PanelHeader from './PanelHeader';
import PanelContent from './PanelContent';

export interface Props {
  expandable?: boolean;
  expanded?: boolean;
  onToggle?: () => void;
}

const Panel = ({
  expandable = false,
  expanded = false,
  onToggle,
  children,
}: PropsWithChildren<Props>) => {
  const handleToggle = () => {
    if (expandable && onToggle) {
      onToggle();
    }
  };

  const contextValue = {
    expandable,
    expanded,
    onToggle: handleToggle,
  };

  return (
    <Styled.Panel>
      <PanelContext.Provider value={contextValue}>{children}</PanelContext.Provider>
    </Styled.Panel>
  );
};

Panel.Title = Styled.Title;

Panel.Header = PanelHeader;

Panel.Content = PanelContent;

export default Panel;
