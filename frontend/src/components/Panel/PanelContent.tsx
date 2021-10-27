import { ReactNode, useContext } from 'react';
import * as Styled from './PanelContent.styles';
import PanelContext from './PanelContext';

interface Props {
  children: ReactNode;
}

const PanelContent = ({ children }: Props): JSX.Element => {
  const { expandable = false, expanded = false } = useContext(PanelContext);

  return (
    <>
      {(!expandable || (expandable && expanded)) && (
        <Styled.Content expanded={expanded}>{children}</Styled.Content>
      )}
    </>
  );
};

export default PanelContent;
