import styled from 'styled-components';

interface LabelProps {
  hasLabel: boolean;
}

export const Label = styled.label<LabelProps>`
  display: block;
  position: relative;
  margin-top: ${({ hasLabel }) => (hasLabel ? '0.5rem' : '0')};
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

export const TextAreaWrapper = styled.div``;

export const TextArea = styled.textarea`
  padding: 0.75rem;
  width: 100%;
  font-size: 1rem;
  font-family: inherit;
  border: 1px solid ${({ theme }) => theme.gray[500]};
  border-radius: 0.125rem;
  background: none;
  outline: none;
  resize: none;

  &:focus {
    border-color: ${({ theme }) => theme.primary[400]};
    box-shadow: inset 0px 0px 0px 1px ${({ theme }) => theme.primary[400]};
  }

  &:disabled {
    color: ${({ theme }) => theme.gray[400]};
  }
`;

export const TextLength = styled.span`
  position: absolute;
  bottom: 0;
  right: 0;
  color: ${({ theme }) => theme.gray[400]};
  font-size: 0.75rem;
  margin: 0.75rem;
`;
