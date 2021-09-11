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
