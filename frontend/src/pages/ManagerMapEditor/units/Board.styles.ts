import styled from 'styled-components';

interface RootSvgProps {
  isDraggable: boolean;
  isDragging: boolean;
}

export const RootSvg = styled.svg<RootSvgProps>`
  cursor: ${({ isDraggable, isDragging }) => {
    if (isDraggable) {
      if (isDragging) return 'grabbing';
      else return 'grab';
    }
    return 'default';
  }};
`;
