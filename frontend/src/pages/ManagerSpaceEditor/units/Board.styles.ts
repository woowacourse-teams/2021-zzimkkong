import styled from 'styled-components';

interface BoardContainerProps {
  isDraggable?: boolean;
  isDragging?: boolean;
}

export const Editor = styled.div`
  flex: 1;
  height: 100%;
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

export const BoardContainerBackground = styled.rect``;

export const Board = styled.svg``;

export const BoardGroup = styled.g``;

export const BoardBackground = styled.rect``;
