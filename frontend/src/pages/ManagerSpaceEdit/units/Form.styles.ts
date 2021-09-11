import styled from 'styled-components';

interface FormContainerProps {
  disabled: boolean;
}

export const Form = styled.form<FormContainerProps>`
  padding: 2rem 1.5rem;
  overflow-y: ${({ disabled }) => (disabled ? 'hidden' : 'auto')};
  flex: 1;
`;

export const FormHeader = styled.h3`
  font-size: 1.5rem;
  margin-bottom: 1.625rem;
`;

export const SpaceSettingHeader = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.75rem;

  ${FormHeader} {
    margin: 0;
  }
`;

export const FormRow = styled.div`
  margin: 2rem 0;
  position: relative;

  &:last-of-type {
    margin-bottom: 0;
  }
`;

export const ColorSelect = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 1.5rem 0;
`;

export const ColorInputLabel = styled.label`
  display: inline-block;
  padding: 0.5rem;
  border: 1px solid ${({ theme }) => theme.gray[500]};
  border-radius: 0.125rem;
  cursor: pointer;
`;

export const ColorInput = styled.input`
  width: 0;
  height: 0;
  opacity: 0;
  padding: 0;
`;

export const ColorDotButton = styled.button`
  background: none;
  border: none;
  padding: 0;
  cursor: pointer;
`;
