import styled from 'styled-components';

export const Page = styled.div`
  display: flex;
`;

export const MapContainer = styled.div`
  height: 100vh;
  flex: 1;
  overflow: auto;
  display: flex;
  flex-direction: column;
  position: relative;
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

export const SpaceRect = styled.rect`
  &:hover {
    opacity: 0.5;
  }
`;

export const SpacePolygon = styled.polygon`
  &:hover {
    opacity: 0.5;
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

export const SelectBox = styled.div`
  display: flex;
  flex-direction: column;
`;

export const SelectButton = styled.button`
  display: flex;
  align-items: center;
  border: none;
  cursor: pointer;
  background-color: white;
  padding: 1rem 1.5rem;
  text-align: left;
  font-size: 1rem;

  svg {
    margin-top: 0.25rem;
    margin-right: 0.5rem;
    fill: ${({ theme }) => theme.gray[400]};
  }

  &:hover {
    opacity: 0.7;
  }

  &:first-child {
    border-bottom: 1px solid rgba(196, 196, 196, 0.3);
  }
`;
