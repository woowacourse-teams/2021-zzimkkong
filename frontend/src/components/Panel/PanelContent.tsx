import { PropsWithChildren, useContext } from 'react';
import PanelContext from './PanelContext';
import * as Styled from './PanelContent.styles';

interface Props {}

const PanelContent = ({ children }: PropsWithChildren<Props>) => {
  const { expandable = false, expanded = false } = useContext(PanelContext);

  return (
    <>{(!expandable || (expandable && expanded)) && <Styled.Content>{children}</Styled.Content>}</>
  );
};

export default PanelContent;
