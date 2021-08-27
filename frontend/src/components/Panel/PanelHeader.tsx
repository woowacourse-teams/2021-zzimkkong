import { PropsWithChildren, useContext } from 'react';
import { ReactComponent as CaretDownIcon } from 'assets/svg/caret-down.svg';
import { Color } from 'types/common';
import PanelContext from './PanelContext';
import * as Styled from './PanelHeader.styles';

export interface Props {
  dotColor?: Color;
}

const PanelHeader = ({ dotColor, children }: PropsWithChildren<Props>): JSX.Element => {
  const { expandable = false, expanded = false, onToggle } = useContext(PanelContext);

  return (
    <Styled.HeaderWrapper expandable={expandable} onClick={onToggle}>
      <Styled.HeaderContent dotColor={dotColor}>{children}</Styled.HeaderContent>
      {expandable && (
        <Styled.Toggle expanded={expanded}>
          <CaretDownIcon />
        </Styled.Toggle>
      )}
    </Styled.HeaderWrapper>
  );
};

export default PanelHeader;
