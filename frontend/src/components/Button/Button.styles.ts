import styled, { css } from 'styled-components';

interface Props {
  variant: 'primary' | 'default';
  size: 'small' | 'medium' | 'large';
  fullWidth: boolean;
}

const variantCSS = {
  primary: css`
    background: ${({ theme }) => theme.primary[400]};
    color: ${({ theme }) => theme.white};
    border: none;
  `,
  default: css`
    background: ${({ theme }) => theme.white};
    border: 1px solid ${({ theme }) => theme.black[400]};
  `,
};

const sizeCSS = {
  small: css`
    padding: 0.25rem 0.5rem;
  `,
  medium: css`
    padding: 0.5rem 0.75rem;
  `,
  large: css`
    padding: 0.75rem 1rem;
    font-size: 1rem;
  `,
};

export const Button = styled.button<Props>`
  ${({ variant }) => variantCSS[variant]}
  ${({ size }) => sizeCSS[size]}
  width: ${({ fullWidth }) => (fullWidth ? '100%' : 'auto')};
  cursor: pointer;
`;
