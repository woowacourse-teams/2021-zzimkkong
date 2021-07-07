import { ReactNode } from 'react';
import styled, { css } from 'styled-components';

interface Props {
  icon?: ReactNode;
  status?: 'success' | 'error' | 'default';
}

const statusCSS = {
  success: css`
    color: ${({ theme }) => theme.green[500]};
  `,
  error: css`
    color: ${({ theme }) => theme.red[500]};
  `,
  default: css`
    color: inherit;
  `,
};

export const Label = styled.label`
  display: block;
  position: relative;
  margin-bottom: 1.625rem;
`;

export const LabelText = styled.span`
  position: absolute;
  display: inline-block;
  top: -0.5rem;
  left: 0.75rem;
  padding: 0 0.25rem;
  font-size: 1rem;
  background-color: white;
`;

export const Icon = styled.div`
  position: absolute;
  display: inline-flex;
  padding: 0 0.5rem;
  height: 100%;
  justify-content: center;
  align-items: center;

  svg,
  img {
    height: 70%;
  }
`;

export const Input = styled.input<Props>`
  padding: 0.75rem;
  width: 100%;
  font-size: 1.25rem;
  line-height: 0.875rem;
  border: 1px solid ${({ theme }) => theme.black[400]};
  outline: none;
  ${({ icon }) => (icon ? 'padding-left: 3rem' : '')};

  &:focus {
    border-color: ${({ theme }) => theme.primary[400]};
    box-shadow: inset 0px 0px 0px 1px ${({ theme }) => theme.primary[400]};
  }
`;

export const Message = styled.p<Props>`
  ${({ status = 'default' }) => statusCSS[status]};
  font-size: 1rem;
  position: absolute;
  bottom: -1.5rem;
  left: 0.75rem;
  height: 1.5rem;
  line-height: 1.5rem;
`;
