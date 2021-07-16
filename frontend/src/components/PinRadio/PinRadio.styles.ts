import styled from 'styled-components';
import { Coordinate } from 'types/common';
import { Color } from 'types/styled';

interface PinProps {
  coordinate: Coordinate;
}

interface PinInputProps {
  color?: Color;
}

export const Pin = styled.label<PinProps>`
  cursor: pointer;
  position: absolute;
  top: ${({ coordinate }) => coordinate.y}px;
  left: ${({ coordinate }) => coordinate.x}px;
  transform: translate(-50%, 0);
`;

export const PinContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
`;

export const PinText = styled.p`
  margin-top: 1.125rem;
  font-size: 0.875rem;
  font-weight: 700;
  white-space: nowrap;
`;

export const PinInput = styled.input<PinInputProps>`
  appearance: none;
  cursor: pointer;
  position: relative;
  box-sizing: border-box;
  margin: 0;

  &::before {
    content: '';
    box-sizing: border-box;
    display: block;
    width: 1.5rem;
    height: 1.5rem;
    padding: 0.125rem;
    border-radius: 50%;
    border: 2px solid ${({ color, theme }) => color ?? theme.primary[400]};
    background-color: ${({ theme }) => theme.white};
    position: absolute;
    top: -0.75rem;
    left: -0.75rem;
    box-shadow: 3px 3px 5px rgba(0, 0, 0, 0.2);
  }

  &:checked::before {
    background-color: ${({ color, theme }) => color ?? theme.primary[400]};
    border: none;
    padding: 0;
  }

  &:checked::after {
    content: '';
    position: absolute;
    top: 50%;
    left: 50%;
    display: block;
    width: 0.375rem;
    height: 0.75rem;
    transform: translate(-55%, -63%) rotate(45deg);
    border-right: 0.2rem solid white;
    border-bottom: 0.2rem solid white;
    cursor: pointer;
  }

  &:disabled::before {
    cursor: not-allowed;
    border: none;
    background-color: ${({ theme }) => theme.gray[400]};
  }

  &:disabled + ${PinText} {
    color: ${({ theme }) => theme.gray[500]};
  }
`;
