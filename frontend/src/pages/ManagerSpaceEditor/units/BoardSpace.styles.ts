import styled from 'styled-components';

interface SpaceRectProps {
  disabled: boolean;
  selected: boolean;
}

export const SpaceRect = styled.rect<SpaceRectProps>`
  opacity: ${({ selected }) => (selected ? '0.5' : '0.3')};
  cursor: ${({ disabled }) => (disabled ? 'default' : 'pointer')};

  &:hover {
    ${({ disabled }) => (disabled ? '' : 'opacity:  0.2;')}
  }
`;

export const SpacePolygon = styled.polygon<SpaceRectProps>`
  opacity: ${({ selected }) => (selected ? '0.5' : '0.3')};
  cursor: ${({ disabled }) => (disabled ? 'default' : 'pointer')};

  &:hover {
    ${({ disabled }) => (disabled ? '' : 'opacity:  0.2;')}
  }
`;

export const SpaceText = styled.text`
  dominant-baseline: middle;
  text-anchor: middle;
  fill: ${({ theme }) => theme.black[700]};
  font-size: 1rem;
  pointer-events: none;
  user-select: none;
`;
