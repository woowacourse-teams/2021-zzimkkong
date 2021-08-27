import styled, { css } from 'styled-components';
import { Coordinate, Color } from 'types/common';

interface PinProps {
  coordinate: Coordinate;
}

interface PinInputProps {
  color?: Color;
}

interface PinTextProps {
  textPosition: 'left' | 'right' | 'top' | 'bottom';
}

export const Pin = styled.label<PinProps>`
  position: absolute;
  top: ${({ coordinate }) => coordinate.y}px;
  left: ${({ coordinate }) => coordinate.x}px;
  transform: translate(-50%, 0);
`;

export const PinContainer = styled.div`
  position: relative;
`;

const PinTextCSS = {
  left: css`
    transform: translateX(calc(-100% - 1.125rem));
  `,
  right: css`
    transform: translateX(1.25rem);
  `,
  top: css`
    transform: translate(-50%, -1.5rem);
  `,
  bottom: css`
    transform: translate(-50%, 1.5rem);
  `,
};

export const PinText = styled.p<PinTextProps>`
  text-align: center;
  position: absolute;
  top: -0.45rem;
  font-size: 0.875rem;
  font-weight: 700;
  white-space: nowrap;
  ${({ textPosition }) => PinTextCSS[textPosition]}
`;

export const PinInput = styled.input<PinInputProps>`
  appearance: none;
  cursor: pointer;
  position: relative;
  box-sizing: border-box;
  display: block;
  margin: 0;

  &::before {
    content: '';
    box-sizing: border-box;
    display: block;
    width: 1.5rem;
    height: 1.5rem;
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

  &:disabled {
    cursor: default;
  }

  &:disabled::before {
    border: none;
    background-color: ${({ theme }) => theme.gray[400]};
  }

  &:disabled + ${PinText} {
    color: ${({ theme }) => theme.gray[500]};
  }
`;
