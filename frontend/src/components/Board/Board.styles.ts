import styled from 'styled-components';

interface RootSvgProps {
  movable: boolean;
  isMoving: boolean;
}

export const RootSvg = styled.svg<RootSvgProps>`
  cursor: ${({ movable, isMoving }) => {
    if (movable) {
      if (isMoving) return 'grabbing';
      else return 'grab';
    }
    return 'default';
  }};
`;

export const BoardContainerBackground = styled.rect``;

export const Board = styled.svg``;

export const BoardGroup = styled.g``;

export const BoardBackground = styled.rect``;
