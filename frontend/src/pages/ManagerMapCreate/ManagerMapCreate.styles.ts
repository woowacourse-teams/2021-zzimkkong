import styled, { createGlobalStyle, css } from 'styled-components';
import Button from 'components/Button/Button';
import IconButton from 'components/IconButton/IconButton';
import Input from 'components/Input/Input';

interface ToolbarButtonProps {
  selected?: boolean;
}

interface BoardContainerProps {
  isDraggable?: boolean;
  isDragging?: boolean;
}

export const PageGlobalStyle = createGlobalStyle`
  body {
    overscroll-behavior: none;
  }
`;

const primaryIconCSS = css`
  svg {
    fill: ${({ theme }) => theme.primary[400]};
  }
`;

export const Container = styled.div`
  padding: 2rem 0;
  display: flex;
  flex-direction: column;
  height: calc(100vh - 3rem);
`;

export const ToolbarButton = styled(IconButton)<ToolbarButtonProps>`
  background-color: ${({ theme, selected }) => (selected ? theme.gray[100] : 'none')};
  border: 1px solid ${({ theme, selected }) => (selected ? theme.gray[400] : 'transparent')};
  border-radius: 0;
  box-sizing: content-box;

  ${({ selected }) => selected && primaryIconCSS}
`;

export const EditorHeader = styled.div``;

export const Form = styled.form`
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 0.75rem;
`;

export const HeaderContent = styled.div``;

export const MapNameContainer = styled.div``;

export const TempSaveContainer = styled.div`
  margin-top: 0.25rem;
`;

export const TempSaveMessage = styled.p`
  display: inline;
  color: ${({ theme }) => theme.gray[400]};
  font-size: 0.875rem;
`;

export const TempSaveButton = styled(Button)`
  padding: 0;
  margin-left: 0.5rem;
  font-size: 0.875rem;
`;

export const MapNameInput = styled.input`
  border: none;
  border-radius: 0.125rem;
  font-size: 1.5rem;
  display: inline-block;
`;

export const MapName = styled.h3`
  font-size: 1.5rem;
  display: inline-block;
`;

export const ButtonContainer = styled.div`
  display: flex;
  gap: 0.5rem;
`;

export const EditorContent = styled.div`
  display: flex;
  flex: 1;
  border-top: 1px solid ${({ theme }) => theme.gray[400]};
  border-bottom: 1px solid ${({ theme }) => theme.gray[400]};
`;

export const Toolbar = styled.div`
  padding: 1rem 0.5rem;
  background-color: ${({ theme }) => theme.gray[100]};
  border-left: 1px solid ${({ theme }) => theme.gray[400]};
  border-right: 1px solid ${({ theme }) => theme.gray[400]};
  display: flex;
  flex-direction: column;
  gap: 1rem;
`;

export const Editor = styled.div`
  flex: 1;
`;

export const BoardContainer = styled.svg<BoardContainerProps>`
  outline: none;

  cursor: ${({ isDraggable, isDragging }) => {
    if (isDraggable) {
      if (isDragging) return 'grabbing';
      else return 'grab';
    }
    return 'default';
  }};
`;

export const InputWrapper = styled.div`
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin: 0 0.25rem;
`;

export const SizeInput = styled(Input)`
  border: 0;
  border-bottom: 2px solid ${({ theme }) => theme.gray[400]};
  border-radius: 0;
  padding: 0 0.25rem;
  margin-bottom: 0.25rem;
  width: 3rem;
  text-align: center;
  color: ${({ theme }) => theme.gray[500]};

  &:focus {
    border-top-width: 0;
    border-left-width: 0;
    border-right-width: 0;
    box-shadow: none;
    color: ${({ theme }) => theme.black[400]};
  }
`;

export const Label = styled.div`
  color: ${({ theme }) => theme.gray[500]};
  text-align: center;
`;

export const LabelIcon = styled.div``;

export const LabelText = styled.div`
  font-size: 0.625rem;
`;
