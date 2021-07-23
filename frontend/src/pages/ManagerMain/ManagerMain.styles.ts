import styled from 'styled-components';

export const PageHeader = styled.div`
  padding: 0.75rem 0;
  display: flex;
  align-items: center;
`;

export const PageTitle = styled.h2`
  font-size: 1.5rem;
  font-weight: 500;
  padding: 0 0.75rem;
  flex-grow: 1;
  text-align: center;
  text-overflow: ellipsis;
  overflow: hidden;
  white-space: nowrap;
`;

export const PanelMessage = styled.p`
  padding: 1rem 0.75rem;
  font-size: 0.875rem;
  color: ${({ theme }) => theme.gray[500]};
`;

export const SpaceList = styled.ul`
  margin: 3rem 0;
`;

export const SpaceListItem = styled.li``;
