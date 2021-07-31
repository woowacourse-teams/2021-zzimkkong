import styled, { css } from 'styled-components';

interface Props {
  size: 'small' | 'medium' | 'large' | 'xLarge';
}

export const Container = styled.div`
  display: inline-flex;
  flex-direction: column;
  align-items: center;
`;

export const Button = styled.button<Props>`
  border: none;
  border-radius: 50%;
  background-color: transparent;
  cursor: pointer;
  display: inline-flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
`;

const iconSizeCSS = {
  small: css`
    width: 1.25rem;
    height: 1.25rem;
  `,
  medium: css`
    width: 1.5rem;
    height: 1.5rem;
  `,
  large: css`
    width: 2.25rem;
    height: 2.25rem;
  `,

  xLarge: css`
    width: 2.75rem;
    height: 2.75rem;
  `,
};

export const IconWrapper = styled.div<Props>`
  ${({ size }) => iconSizeCSS[size]}
  overflow: hidden;
`;

const textSizeCSS = {
  small: css`
    font-size: 0.5rem;
  `,
  medium: css`
    font-size: 0.5rem;
  `,
  large: css`
    font-size: 0.75rem;
  `,
  xLarge: css`
    font-size: 0.875rem;
  `,
};

export const Text = styled.div<Props>`
  ${({ size }) => textSizeCSS[size]}
  user-select: none;
  white-space: nowrap;
  text-align: center;
`;
