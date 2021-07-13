import { PropsWithChildren, useContext } from 'react';
import { ReactComponent as CaretDownIcon } from 'assets/svg/caret-down.svg';
import { Color } from 'types/styled';
import PanelContext from './PanelContext';
import * as Styled from './PanelHeader.styles';

export interface Props {
  bgColor?: Color;
}

const PanelHeader = ({ bgColor, children }: PropsWithChildren<Props>): JSX.Element => {
  const { expandable = false, expanded = false, onToggle } = useContext(PanelContext);

  return (
    <Styled.HeaderWrapper bgColor={bgColor} expandable={expandable} onClick={onToggle}>
      <Styled.HeaderContent>{children}</Styled.HeaderContent>
      {expandable && (
        <Styled.Toggle expanded={expanded}>
          <CaretDownIcon />
        </Styled.Toggle>
      )}
    </Styled.HeaderWrapper>
  );
};

export default PanelHeader;
