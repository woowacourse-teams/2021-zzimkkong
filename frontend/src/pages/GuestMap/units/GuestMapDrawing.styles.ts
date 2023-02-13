import styled, { css } from 'styled-components';

interface SpaceElementProps {
  disabled: boolean;
}

const disabledCSS = css`
  opacity: 0.2;
  pointer-events: none;
  filter: brightness(0.5);

  &:hover {
    opacity: 0.2;
  }
`;

export const MapItem = styled.div<{ width: number; height: number }>`
  flex: 1;
  padding: 1.5rem;
  min-width: ${({ width }) => width}px;
  min-height: ${({ height }) => height}px;
  display: flex;
  justify-content: center;
  align-items: center;

  @media (max-width: ${({ width }) => width}px) {
    position: absolute;
    top: 0;
    left: 0;
  }
`;

export const Space = styled.g`
  cursor: pointer;
`;

export const SpaceRect = styled.rect<SpaceElementProps>`
  &:hover {
    opacity: 0.7;
  }

  ${({ disabled }) => disabled && disabledCSS};
`;

export const SpacePolygon = styled.polygon<SpaceElementProps>`
  &:hover {
    opacity: 0.7;
  }

  ${({ disabled }) => disabled && disabledCSS};
`;

export const SpaceAreaText = styled.text`
  dominant-baseline: middle;
  text-anchor: middle;
  fill: ${({ theme }) => theme.black[700]};
  font-size: 1rem;
  pointer-events: none;
  user-select: none;
`;
