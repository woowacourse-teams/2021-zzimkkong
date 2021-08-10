import styled from 'styled-components';

interface ContainerProps {
  open: boolean;
}

export const Container = styled.div<ContainerProps>`
  display: ${({ open }) => (open ? 'flex' : 'none')};
  flex-wrap: wrap;
  gap: 0.75rem;
  width: 9.875rem;
  height: 7.5rem;
  border: 1px solid ${({ theme }) => theme.gray[400]};
  padding: 0.75rem;
  background-color: ${({ theme }) => theme.white};
`;

export const InputWrapper = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
`;

export const Input = styled.input`
  padding: 0;
  height: 1.25rem;
  width: 5rem;
  text-align: center;
  border: none;
  border-bottom: 1px solid ${({ theme }) => theme.gray[400]};
  outline: none;
`;

export const ColorInputLabel = styled.label`
  display: inline-block;
  margin-left: 0.75rem;
  cursor: pointer;
`;

export const ColorInput = styled.input`
  width: 0;
  height: 0;
  opacity: 0;
  padding: 0;
`;
