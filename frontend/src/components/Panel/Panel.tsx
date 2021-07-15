import { PropsWithChildren, useState } from 'react';
import * as Styled from './Panel.styles';
import PanelContent from './PanelContent';
import PanelContext from './PanelContext';
import PanelHeader from './PanelHeader';

export interface Props {
  expandable?: boolean;
  initialExpanded?: boolean;
}

const Panel = ({
  expandable = false,
  initialExpanded = false,
  children,
}: PropsWithChildren<Props>): JSX.Element => {
  const [expanded, setExpanded] = useState(initialExpanded);

  const handleToggle = () => {
    if (expandable) {
      setExpanded((prev) => !prev);
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

Panel.Inner = Styled.Inner;

Panel.Header = PanelHeader;

Panel.Content = PanelContent;

export default Panel;
