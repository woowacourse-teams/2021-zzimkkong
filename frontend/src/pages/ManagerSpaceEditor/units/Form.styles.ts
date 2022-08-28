import styled from 'styled-components';
import Button from 'components/Button/Button';
import IconButton from 'components/IconButton/IconButton';

interface FormContainerProps {
  disabled: boolean;
}

interface TabListItemProps {
  isPrimary: boolean;
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

export const TabList = styled.ul`
  width: 100%;
  display: grid;
  grid-gap: 4px;
  grid-template-columns: 1fr 1fr 1fr;
  grid-row: 1;
  margin-bottom: 0.5rem;
`;

export const TabListItem = styled.li<TabListItemProps>`
  display: flex;
  justify-content: stretch;
  align-items: center;
  border: 1px solid ${({ theme }) => theme.gray[500]};
  overflow: hidden;
  white-space: nowrap;
  border-radius: 0.25rem;

  background-color: ${({ isPrimary, theme }) => isPrimary && theme.primary[400]};
  color: ${({ isPrimary, theme }) => isPrimary && theme.white};

  svg {
    fill: ${({ isPrimary, theme }) => isPrimary && theme.white};
  }
`;

export const TabTextButton = styled(Button)`
  flex: 1;
  font-size: 0.75rem;
`;

export const TabRemoveButton = styled(IconButton)`
  display: block;
  border-radius: 50%;
  margin-right: 4px;

  &:hover {
    background-color: ${({ theme }) => theme.gray[200]};

    svg {
      fill: ${({ theme }) => theme.black[400]};
    }
  }

  svg {
    width: 10px;
    height: 10px;
  }
`;

export const TabCreateButton = styled(Button)`
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 25.5px;
  border-radius: 0;

  svg {
    width: 10px;
    height: 10px;
  }

  &:hover {
    background-color: ${({ theme }) => theme.gray[200]};
  }
`;
