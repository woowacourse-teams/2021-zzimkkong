import styled, { css } from 'styled-components';

interface Props {
  variant: 'primary' | 'primary-text' | 'text' | 'default';
  size: 'small' | 'medium' | 'large';
  fullWidth: boolean;
}

const variantCSS = {
  primary: css`
    background: ${({ theme }) => theme.primary[400]};
    color: ${({ theme }) => theme.white};
    border: none;

    &:disabled {
      background: ${({ theme }) => theme.primary[100]};
    }
  `,
  'primary-text': css`
    background: none;
    border: none;
    color: ${({ theme }) => theme.primary[400]};
  `,
  text: css`
    background: none;
    border: none;
  `,
  default: css`
    background: ${({ theme }) => theme.white};
    border: 1px solid ${({ theme }) => theme.black[400]};

    &:disabled {
      color: ${({ theme }) => theme.gray[400]};
      border: 1px solid ${({ theme }) => theme.gray[400]};
    }
  `,
};

const sizeCSS = {
  small: css`
    padding: 0.25rem 0.5rem;
  `,
  medium: css`
    padding: 0.625rem 0.875rem;
  `,
  large: css`
    padding: 0.75rem 1rem;
    font-size: 1.25rem;
    font-weight: 700;
  `,
};

export const Button = styled.button<Props>`
  ${({ variant }) => variantCSS[variant]}
  ${({ size }) => sizeCSS[size]}
  width: ${({ fullWidth }) => (fullWidth ? '100%' : 'auto')};
  border-radius: 0.125rem;
  cursor: pointer;
`;
