import styled from 'styled-components';
import { Color } from 'types/styled';
import { getTextColor } from 'utils/color';

interface HeaderWrapperProps {
  bgColor?: Color;
  expandable: boolean;
}

interface ToggleProps {
  expanded: boolean;
}

export const HeaderWrapper = styled.div<HeaderWrapperProps>`
  background-color: ${({ bgColor = 'transparent' }) => bgColor};
  color: ${({ theme, bgColor = 'transparent' }) =>
    getTextColor(bgColor) === 'black' ? theme.black[500] : theme.white};
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.8rem;
  cursor: ${({ expandable }) => (expandable ? 'pointer' : 'default')};
  gap: 1rem;

  path {
    fill: ${({ theme, bgColor = 'transparent' }) =>
      getTextColor(bgColor) === 'black' ? theme.black[500] : theme.white};
  }
`;

export const HeaderContent = styled.div`
  flex: 1;
`;

export const Toggle = styled.div<ToggleProps>`
  background: none;
  border: none;
  line-height: 0;
  transform: ${({ expanded }) => (expanded ? 'rotate(180deg)' : 'rotate(0deg)')};
`;
