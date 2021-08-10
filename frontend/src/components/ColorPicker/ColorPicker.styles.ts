import styled from 'styled-components';
import { WithControl } from './../MapListItem/MapListItem.stories';

interface PickerWrapperProps {
  open: boolean;
}

interface PickerIconProps {
  color: string;
}

export const Container = styled.div`
  display: flex;
`;

export const PickerWrapper = styled.div<PickerWrapperProps>`
  display: ${({ open }) => (open ? 'flex' : 'none')};
  flex-wrap: wrap;
  gap: 0.375rem;
  position: absolute;
  width: 9rem;
  border: 1px solid ${({ theme }) => theme.gray[400]};
  padding: 0.75rem;
  margin-top: -0.75rem;
  margin-left: 2.875rem;
  background-color: ${({ theme }) => theme.white};
`;

export const PickerIcon = styled.div<PickerIconProps>`
  width: 1.5rem;
  height: 1.5rem;
  border: 1px solid ${({ color }) => color};
  border-radius: 50%;
  cursor: pointer;
  background-color: ${({ color }) => color};
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

  &:focus {
    outline: none;
  }
`;

export const ColorInputLabel = styled.label`
  display: inline-block;
  margin-left: 0.5rem;
  cursor: pointer;
`;

export const ColorInput = styled.input`
  width: 0;
  height: 0;
  opacity: 0;
  padding: 0;
`;
