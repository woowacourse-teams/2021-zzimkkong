import styled, { createGlobalStyle } from 'styled-components';

export const MapCreateGlobalStyle = createGlobalStyle`
  body {
    overscroll-behavior: none;
  }
`;

export const Container = styled.div`
  padding: 2rem 0;
  display: flex;
  flex-direction: column;
  height: calc(100vh - 3rem);
`;

export const Form = styled.form`
  display: flex;
  flex-direction: column;
  flex: 1;
`;

export const FormHeader = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 0.75rem;
`;

export const FormControl = styled.div`
  display: flex;
  gap: 0.5rem;
`;

export const MapNameInput = styled.input`
  border: none;
  border-radius: 0.125rem;
  font-size: 1.5rem;
  display: inline-block;
  border: 2px solid transparent;

  &:hover {
    border-color: ${({ theme }) => theme.primary[400]};
  }

  &:focus {
    outline-color: ${({ theme }) => theme.primary[400]};
  }
`;
