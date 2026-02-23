import styled from 'styled-components';

export const Container = styled.div`
  display: flex;
  justify-content: center;
  gap: 8px;
  overflow-x: auto;
  padding: 4px;

  /* 스크롤바 스타일링 */
  &::-webkit-scrollbar {
    height: 6px;
  }

  &::-webkit-scrollbar-track {
    background: #f1f1f1;
    border-radius: 3px;
  }

  &::-webkit-scrollbar-thumb {
    background: #ccc;
    border-radius: 3px;
  }

  &::-webkit-scrollbar-thumb:hover {
    background: #999;
  }
`;

export const GroupButton = styled.button<{ selected: boolean }>`
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0.625rem 0.875rem;
  border: ${({ theme, selected }) =>
    selected ? 'none' : `1px solid ${theme.primary[400] ?? '#FF8833'}`};
  border-radius: 0.125rem;
  background-color: ${({ theme, selected }) => (selected ? theme.primary[400] : theme.white)};
  color: ${({ theme, selected }) => (selected ? theme.white : theme.primary[400])};
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.15s ease;
  white-space: nowrap;
  min-width: 120px;

  &:hover {
    opacity: 0.85;
  }

  &:active {
    transform: scale(0.98);
  }
`;
