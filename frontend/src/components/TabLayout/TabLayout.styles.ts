import styled from 'styled-components';

export const Container = styled.div`
  height: 220px;
  padding-top: 60px;
  background-color: ${({ theme }) => theme.primary[50]};
`;

export const TabContainer = styled.div`
  width: 100%;
  display: flex;
  flex-direction: column;
`;

export const Content = styled.div`
  height: 300px;
  background-color: ${({ theme }) => theme.white};
`;
