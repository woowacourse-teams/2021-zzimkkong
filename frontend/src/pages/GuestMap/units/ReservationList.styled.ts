import styled from 'styled-components';

export const ReservationListWrapper = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1rem;
`;

export const PanelHeadWrapper = styled.div`
  width: 100%;
  display: flex;
  justify-content: space-between;
`;

export const PanelMessage = styled.p`
  padding: 1rem 0.75rem;
  font-size: 0.875rem;
  color: ${({ theme }) => theme.gray[500]};
`;

export const IconButtonWrapper = styled.div`
  display: flex;
  gap: 0.5rem;
`;
