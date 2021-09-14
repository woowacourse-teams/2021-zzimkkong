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
    color: ${({ theme }) => theme.gray[500]};
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
  top: -0.375rem;
  left: 0.75rem;
  padding: 0 0.25rem;
  font-size: 0.75rem;
  background-color: white;
  color: ${({ theme }) => theme.gray[500]};
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
  font-size: 1rem;
  border: 1px solid ${({ theme }) => theme.gray[500]};
  border-radius: 0.125rem;
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
  margin: 0.25rem 0.5rem;
`;
