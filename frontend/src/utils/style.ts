import { css } from 'styled-components';
import { Color } from 'types/styled';
import { getTextColor } from './color';

interface ResponsiveColorProps {
  bgColor?: Color;
}

export const responsiveTextColor = css<ResponsiveColorProps>`
  color: ${({ theme, bgColor = 'transparent' }) =>
    getTextColor(bgColor) === 'black' ? theme.black[500] : theme.white};
`;

export const responsiveFill = css<ResponsiveColorProps>`
  fill: ${({ theme, bgColor = 'transparent' }) =>
    getTextColor(bgColor) === 'black' ? theme.black[500] : theme.white};
`;
