import styled, { css } from 'styled-components';
import { Z_INDEX } from 'constants/style';

interface TimeContainerProps {
  labelText?: string;
  isOptionOpen: boolean;
}

interface TimeButtonProps {
  selected: boolean;
}

const labelTextCSS = (labelText: string) => css`
  margin-top: 0.375rem;

  &::before {
    content: '${labelText}';
    display: block;
    position: absolute;
    top: -0.375rem;
    left: 0.75rem;
    padding: 0 0.25rem;
    font-size: 0.75rem;
    background-color: white;
    color: ${({ theme }) => theme.gray[500]};
  }
`;

export const Container = styled.div`
  position: relative;
`;

export const TimeContainer = styled.div<TimeContainerProps>`
  height: 2.875rem;
  display: flex;
  justify-content: space-evenly;
  align-items: center;
  border: 1px solid ${({ theme }) => theme.gray[500]};
  border-radius: ${({ isOptionOpen }) => (isOptionOpen ? '0.125rem 0.125rem 0 0' : '0.125rem')};
  position: relative;
  box-sizing: content-box;
  z-index: ${Z_INDEX.TIME_PICKER};

  ${({ labelText }) => (labelText ? labelTextCSS(labelText) : '')}
`;

export const TimeButton = styled.button<TimeButtonProps>`
  padding: 0 0.5rem;
  display: flex;
  align-items: center;
  height: inherit;
  border: 0;
  background-color: transparent;
  letter-spacing: 1px;
  font-size: 1rem;
  cursor: pointer;

  ${({ selected, theme }) =>
    selected ? `box-shadow: inset 0 -3px ${theme.primary[400] as string};` : ''}

  &:focus {
    outline-color: ${({ theme }) => theme.primary[400]};
  }
`;

export const OptionsContainer = styled.div`
  width: 100%;
  position: absolute;
  user-select: none;
`;

export const Dimmer = styled.div`
  width: 100vh;
  height: 100vh;
  position: fixed;
  top: 0;
  right: 0;
  left: 0;
  bottom: 0;
  z-index: ${Z_INDEX.TIME_PICKER_DIMMER};
`;
