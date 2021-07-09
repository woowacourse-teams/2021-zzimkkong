import { PropsWithChildren } from 'react';
import * as Styled from './Panel.styles';
import PanelContext from './PanelContext';
import PanelHeader from './PanelHeader';
import PanelContent from './PanelContent';

export interface Props {
  expandable?: boolean;
  expanded?: boolean;
}

const Panel = ({ expandable = false, expanded = false, children }: PropsWithChildren<Props>) => {
  const contextValue = {
    expandable,
    expanded,
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
