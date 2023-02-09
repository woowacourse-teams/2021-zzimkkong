import styled from 'styled-components';

export const Container = styled.div`
  display: inline-block;
`;

export const ButtonContainer = styled.div`
  padding: 0.375rem 0.75rem;
  display: flex;
  border-radius: 3rem;
  background-color: ${({ theme }) => theme.gray[100]};
`;

export const SwitchButton = styled.button<{ selected: boolean }>`
  padding: 0.5rem 1.25rem;
  margin-right: 0.5rem;
  border: 0;
  border-radius: 3rem;
  font-size: 1rem;
  background-color: ${({ theme, selected }) => (selected ? theme.primary[500] : theme.gray[100])};
  color: ${({ theme, selected }) => (selected ? theme.white : theme.black[500])};
  cursor: pointer;

  &:last-of-type {
    margin-right: 0;
  }
`;
