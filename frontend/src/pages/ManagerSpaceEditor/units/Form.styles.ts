import styled from 'styled-components';
import Button from 'components/Button/Button';

interface FormContainerProps {
  disabled: boolean;
}

export const Form = styled.form<FormContainerProps>`
  padding: 2rem 1.5rem;
  overflow-y: ${({ disabled }) => (disabled ? 'hidden' : 'auto')};
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2rem;
`;

export const Section = styled.section``;

export const Title = styled.h3`
  font-size: 1.25rem;
`;

export const TitleContainer = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
`;

export const ContentsContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1.375rem;
`;

export const Row = styled.div``;

export const ColorSelect = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
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

export const InputWrapper = styled.div`
  display: flex;
  gap: 1rem;

  label {
    flex: 1;
  }
`;

export const InputMessage = styled.p`
  font-size: 0.75rem;
  margin: 0.25rem 0.5rem;
  color: ${({ theme }) => theme.gray[400]};
`;

export const Label = styled.div`
  font-size: 0.75rem;
  color: ${({ theme }) => theme.gray[500]};
`;

export const Fieldset = styled.div`
  position: relative;
  border: 1px solid ${({ theme }) => theme.gray[500]};
  border-radius: 0.125rem;
  padding: 1rem 0.75rem;
  margin-top: 0.375rem;

  ${Label} {
    position: absolute;
    top: -0.375rem;
    left: 0.75rem;
    padding: 0 0.25rem;
    background-color: ${({ theme }) => theme.white};
  }
`;

export const FormSubmitContainer = styled.div`
  display: flex;
  justify-content: flex-end;
`;

export const DeleteButton = styled(Button)`
  color: ${({ theme }) => theme.red[900]};
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;

  svg {
    width: 1rem;
    height: 1rem;
    vertical-align: middle;
    fill: ${({ theme }) => theme.red[900]};
  }
`;
