import styled, { css } from 'styled-components';
import { Color } from 'types/styled';
import { getTextColor } from 'utils/color';

interface ResponsiveColorProps {
  bgColor?: Color;
}

interface HeaderWrapperProps extends ResponsiveColorProps {
  expandable: boolean;
}

interface ToggleProps {
  expanded: boolean;
}

const responsiveTextColor = css<ResponsiveColorProps>`
  color: ${({ theme, bgColor = 'transparent' }) =>
    getTextColor(bgColor) === 'black' ? theme.black[500] : theme.white};
`;

const responsiveFill = css<ResponsiveColorProps>`
  fill: ${({ theme, bgColor = 'transparent' }) =>
    getTextColor(bgColor) === 'black' ? theme.black[500] : theme.white};
`;

export const HeaderWrapper = styled.div<HeaderWrapperProps>`
  ${responsiveTextColor};
  background-color: ${({ bgColor = 'transparent' }) => bgColor};
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: ${({ expandable }) => (expandable ? 'pointer' : 'default')};
  gap: 1rem;

  path {
    ${responsiveFill};
  }
`;

export const HeaderContent = styled.div`
  flex: 1;
`;

export const Toggle = styled.div<ToggleProps>`
  margin-right: 0.75rem;
  background: none;
  border: none;
  line-height: 0;
  transform: ${({ expanded }) => (expanded ? 'rotate(180deg)' : 'rotate(0deg)')};
`;
