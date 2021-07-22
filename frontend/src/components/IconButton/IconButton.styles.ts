import styled, { css } from 'styled-components';

interface ButtonProps {
  size: 'small' | 'medium' | 'large';
  variant: 'primary' | 'gray' | 'default';
}

interface TextProps {
  size: 'small' | 'medium' | 'large';
}

export const Container = styled.div`
  display: inline-flex;
  flex-direction: column;
  align-items: center;
`;

const buttonSizeCSS = {
  small: css`
    width: 1.25rem;
    height: 1.25rem;
    padding: 0;
  `,
  medium: css`
    width: 1.625rem;
    height: 1.625rem;
    padding: 0.125rem;
  `,
  large: css`
    width: 2.25rem;
    height: 2.25rem;
    padding: 0.25rem;
  `,
};

const buttonVariantCSS = {
  default: css`
    fill: ${({ theme }) => theme.black[400]};
  `,
  gray: css`
    fill: ${({ theme }) => theme.gray[500]};
  `,
  primary: css`
    fill: ${({ theme }) => theme.primary[400]};
  `,
};

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
};

export const Button = styled.button<ButtonProps>`
  ${({ size }) => buttonSizeCSS[size]}
  border: none;
  border-radius: 50%;
  background-color: transparent;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;

  svg,
  img {
    ${({ variant }) => buttonVariantCSS[variant]};
    width: 100%;
    height: 100%;
  }
`;

export const Text = styled.div<TextProps>`
  ${({ size }) => textSizeCSS[size]}
  cursor: default;
  user-select: none;
`;
