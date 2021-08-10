import styled, { css } from 'styled-components';
import Button from 'components/Button/Button';
import IconButton from 'components/IconButton/IconButton';
import { Z_INDEX } from 'constants/style';
import { Color } from 'types/common';

interface ToolbarButtonProps {
  selected?: boolean;
}

interface ColorDotProps {
  color: Color;
  size: 'medium' | 'large';
}

interface WeekdayProps {
  value?: 'Saturday' | 'Sunday';
}

interface BoardContainerProps {
  isDraggable?: boolean;
  isDragging?: boolean;
}

interface FormContainerProps {
  disabled: boolean;
}

interface SpaceAreaRectProps {
  disabled: boolean;
}

export const Page = styled.div`
  padding: 2rem 0;
  display: flex;
  flex-direction: column;
  gap: 1rem;
  height: calc(100vh - 3rem);
`;

export const EditorHeader = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
`;

export const MapName = styled.h3`
  font-size: 1.5rem;
`;

export const ButtonContainer = styled.div``;

export const EditorContainer = styled.div`
  flex: 1;
  display: flex;
  justify-content: space-between;
  gap: 3rem;
  overflow: hidden;
`;

export const EditorContent = styled.div`
  position: relative;
  display: flex;
  flex: 2;
  height: 100%;
  border: 1px solid ${({ theme }) => theme.gray[300]};
  border-radius: 0.25rem;
`;

export const Editor = styled.div`
  flex: 1;
  height: 100%;
`;

export const FormContainer = styled.div<FormContainerProps>`
  position: relative;
  display: flex;
  flex-direction: column;
  flex: 1;
  height: 100%;
  min-width: 20rem;
  border: 1px solid ${({ theme }) => theme.gray[300]};
  border-radius: 0.25rem;

  &::before {
    ${({ disabled }) => (disabled ? `content: ''` : '')};
    position: absolute;
    top: 0;
    left: 0;
    display: block;
    width: 100%;
    height: 100%;
    background-color: ${({ theme }) => theme.gray[200]};
    opacity: 0.3;
    z-index: ${Z_INDEX.MODAL_OVERLAY};
  }
`;

export const Form = styled.form<FormContainerProps>`
  padding: 2rem 1.5rem;
  overflow-y: ${({ disabled }) => (disabled ? 'hidden' : 'auto')};
  flex: 1;
`;

export const FormHeader = styled.h4`
  font-size: 1.5rem;
  margin-bottom: 1.625rem;
`;

export const FormRow = styled.div`
  margin: 2rem 0;
  position: relative;

  &:last-of-type {
    margin-bottom: 0;
  }
`;

export const FormLabel = styled.div`
  font-size: 0.75rem;
  color: ${({ theme }) => theme.gray[500]};
`;

export const FormSubmitContainer = styled.div`
  display: flex;
  justify-content: flex-end;
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

export const SpaceSelect = styled.div`
  border-bottom: 1px solid ${({ theme }) => theme.gray[300]};
  padding: 1.5rem;
  position: relative;
`;

export const SpaceSelectWrapper = styled.div`
  margin-bottom: 0.5rem;
`;

export const SpaceOption = styled.div`
  display: flex;
  align-items: center;
  gap: 0.75rem;
`;

export const AddButtonWrapper = styled.div`
  display: inline-block;
  position: absolute;
  right: 1.5rem;
  bottom: -1.25rem;
  z-index: ${Z_INDEX.SPACE_ADD_BUTTON};
`;

export const ColorSelect = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 1.5rem 0;
`;

export const PresetSelect = styled.div`
  display: flex;
  gap: 0.5rem;
`;

export const PresetSelectWrapper = styled.div`
  flex: 1;
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
  margin: 0.25rem 0.75rem;
  color: ${({ theme }) => theme.gray[500]};
`;

export const WeekdaySelect = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
`;

export const WeekdayLabel = styled.label`
  display: inline-flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
`;

const weekdayColorCSS = {
  default: css``,
  Saturday: css`
    color: ${({ theme }) => theme.blue[900]};
  `,
  Sunday: css`
    color: ${({ theme }) => theme.red[500]};
  `,
};

export const Weekday = styled.span<WeekdayProps>`
  ${({ value }) => weekdayColorCSS[value ?? 'default']};
`;

export const ColorDotButton = styled.button`
  background: none;
  border: none;
  padding: 0;
  cursor: pointer;
`;

const colorDotSizeCSS = {
  medium: css`
    width: 1rem;
    height: 1rem;
  `,
  large: css`
    width: 1.5rem;
    height: 1.5rem;
  `,
};

export const ColorDot = styled.span<ColorDotProps>`
  display: inline-block;
  background-color: ${({ color }) => color};
  border-radius: 50%;
  ${({ size }) => colorDotSizeCSS[size]};
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

export const RadioLabel = styled.label``;

export const RadioInput = styled.input``;

export const RadioLabelText = styled.span``;

export const Fieldset = styled.div`
  border: 1px solid ${({ theme }) => theme.gray[500]};
  border-radius: 0.125rem;
  padding: 1rem 0.75rem;

  ${FormLabel} {
    position: absolute;
    top: -0.375rem;
    left: 0.75rem;
    padding: 0 0.25rem;
    background-color: ${({ theme }) => theme.white};
  }
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

export const BoardContainer = styled.svg<BoardContainerProps>`
  cursor: ${({ isDraggable, isDragging }) => {
    if (isDraggable) {
      if (isDragging) return 'grabbing';
      else return 'grab';
    }
    return 'default';
  }};
`;

export const Toolbar = styled.div`
  padding: 1rem 0.5rem;
  background-color: ${({ theme }) => theme.gray[100]};
  border-right: 1px solid ${({ theme }) => theme.gray[300]};
  display: flex;
  flex-direction: column;
  gap: 1rem;
`;

const primaryIconCSS = css`
  svg {
    fill: ${({ theme }) => theme.primary[400]};
  }
`;

export const ToolbarButton = styled(IconButton)<ToolbarButtonProps>`
  background-color: ${({ theme, selected }) => (selected ? theme.gray[100] : 'none')};
  border: 1px solid ${({ theme, selected }) => (selected ? theme.gray[400] : 'transparent')};
  border-radius: 0;
  box-sizing: content-box;
  ${({ selected }) => selected && primaryIconCSS}
`;

export const SpaceShapeSelect = styled.div`
  position: absolute;
  top: 0.5rem;
  left: 50%;
  transform: translateX(-50%);
  margin: 0 auto;
  padding: 0.5rem 1rem;
  background-color: ${({ theme }) => theme.gray[100]};
  border-right: 1px solid ${({ theme }) => theme.gray[300]};
  display: flex;
  gap: 1rem;
`;

export const NoSpaceMessage = styled.p`
  text-align: center;
  font-size: 1.125rem;
  margin: 3rem auto;
`;

export const SpaceAreaRect = styled.rect<SpaceAreaRectProps>`
  opacity: 0.3;
  cursor: ${({ disabled }) => (disabled ? 'default' : 'pointer')};

  &:hover {
    opacity: ${({ disabled }) => (disabled ? '0.3' : '0.2')};
  }
`;

export const SpaceAreaText = styled.text`
  dominant-baseline: middle;
  text-anchor: middle;
  fill: ${({ theme }) => theme.black[700]};
  font-size: 1rem;
  pointer-events: none;
  user-select: none;
`;
