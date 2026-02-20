import styled from 'styled-components';

export const Container = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
`;

export const Label = styled.label<{ borderColor?: string }>`
  display: inline-flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  padding: 8px;
  border: 2px solid ${({ borderColor }) => borderColor || '#ccc'};
  border-radius: 4px;
  margin: 0 4px;
`;

export const DisplayName = styled.span`
  margin-bottom: 4px;
`;
