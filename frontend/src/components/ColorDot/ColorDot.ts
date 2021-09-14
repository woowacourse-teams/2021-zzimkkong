import styled, { css } from 'styled-components';
import { Color } from 'types/common';

interface ColorDotProps {
  color: Color;
  size?: 'medium' | 'large';
}

const colorDotSizeCSS = {
  medium: css`
    width: 1rem;
    height: 1rem;
  `,
  large: css`
    width: 1.5rem;
    height: 1.5rem;
  `,
};

const ColorDot = styled.span<ColorDotProps>`
  display: inline-block;
  background-color: ${({ color }) => color};
  border-radius: 50%;
  ${({ size = 'medium' }) => colorDotSizeCSS[size]};
`;

export default ColorDot;
