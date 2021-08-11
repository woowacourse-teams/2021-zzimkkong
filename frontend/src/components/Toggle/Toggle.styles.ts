import styled, { css } from 'styled-components';

interface InputProps {
  variant: 'default' | 'primary';
}

export const Slider = styled.span`
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border-radius: 2.25rem;
  background-color: ${({ theme }) => theme.gray[400]};
  transition: background-color 0.4s, transform 0.4s;

  &::before {
    content: '';
    position: absolute;
    height: 1.5rem;
    width: 1.5rem;
    left: 0;
    bottom: -0.375rem;
    border-radius: 1.5rem;
    background-color: ${({ theme }) => theme.white};
    box-shadow: 1px 1px 1px 1px ${({ theme }) => theme.shadow};
    transition: background-color 0.2s, transform 0.2s;
  }
`;

export const Label = styled.label`
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
`;

export const LabelText = styled.span`
  font-size: 0.875rem;
  color: ${({ theme }) => theme.gray[500]};
`;

export const Toggle = styled.div`
  position: relative;
  display: inline-block;
  width: 3rem;
  height: 0.75rem;
`;

const variantCSS = {
  primary: css`
    &:checked + ${Slider} {
      background-color: ${({ theme }) => theme.primary[400]};
    }

    &:focus + ${Slider} {
      box-shadow: 0 0 1px ${({ theme }) => theme.primary[400]};
    }
  `,
  default: css`
    &:checked + ${Slider} {
      background-color: ${({ theme }) => theme.green[400]};
    }

    &:focus + ${Slider} {
      box-shadow: 0 0 1px ${({ theme }) => theme.green[400]};
    }
  `,
};

export const Input = styled.input<InputProps>`
  opacity: 0;
  width: 0;
  height: 0;

  ${({ variant }) => variantCSS[variant]};

  &:checked + ${Slider}:before {
    transform: translateX(1.5rem);
  }
`;
