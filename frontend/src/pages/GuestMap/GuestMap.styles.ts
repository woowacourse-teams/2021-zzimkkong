import styled from 'styled-components';

export const Page = styled.div`
  display: flex;
  flex-direction: column;
  height: calc(100vh - 3rem);
`;

export const PageWithBottomButton = styled.div<{ hasBottomButton: boolean }>`
  margin-bottom: ${({ hasBottomButton }) => (hasBottomButton ? '5rem' : '2rem')};
`;

export const PageTitle = styled.h2`
  font-size: 1.5rem;
  font-weight: 700;
  margin: 1.5rem auto;
`;

export const MapContainer = styled.div`
  width: 100%;
  flex: 1;
  padding: 1rem;
  margin: 1rem 0 1.5rem;
  overflow: auto;
  display: flex;
  justify-content: center;
  position: relative;
`;

export const MapContainerInner = styled.div<{ width: number; height: number }>`
  min-width: ${({ width }) => width}px;
  min-height: ${({ height }) => height}px;
  display: flex;
  justify-content: center;

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

export const DeleteModalContainer = styled.div`
  display: flex;
  justify-content: flex-end;
  margin-top: 1.5rem;
`;
