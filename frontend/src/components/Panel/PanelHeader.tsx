import { PropsWithChildren, useContext } from 'react';
import * as Styled from './PanelHeader.styles';
import { ReactComponent as CaretDownIcon } from 'assets/svg/caret-down.svg';
import { Color } from 'types/styled';
import PanelContext from './PanelContext';

export interface Props {
  bgColor?: Color;
}

const PanelHeader = ({ bgColor, children }: PropsWithChildren<Props>) => {
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
