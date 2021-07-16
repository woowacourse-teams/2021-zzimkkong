import { ReactNode } from 'react';
import styled, { css } from 'styled-components';

interface LabelProps {
  hasMessage: boolean;
  hasLabel: boolean;
}

interface InputProps {
  icon: ReactNode;
}

interface MessageProps {
  status: 'success' | 'error' | 'default';
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

export const Label = styled.label<LabelProps>`
  display: block;
  position: relative;
  margin-top: ${({ hasLabel }) => (hasLabel ? '0.5rem' : '0')};
  margin-bottom: ${({ hasMessage }) => (hasMessage ? '1.625rem' : '0')};
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
  display: flex;
  width: 3rem;
  height: 100%;
  justify-content: center;
  align-items: center;
  padding: 0.75rem 0.5rem;
`;

export const Input = styled.input<InputProps>`
  padding: 0.75rem;
  width: 100%;
  font-size: 1.25rem;
  border: 1px solid ${({ theme }) => theme.black[400]};
  background: none;
  outline: none;
  ${({ icon }) => (icon ? 'padding-left: 3rem' : '')};

  &:focus {
    border-color: ${({ theme }) => theme.primary[400]};
    box-shadow: inset 0px 0px 0px 1px ${({ theme }) => theme.primary[400]};
  }
`;

export const Message = styled.p<MessageProps>`
  ${({ status = 'default' }) => statusCSS[status]};
  font-size: 0.75rem;
  position: absolute;
  bottom: -1.5rem;
  left: 0.75rem;
  height: 1.5rem;
  line-height: 1.5rem;
`;
