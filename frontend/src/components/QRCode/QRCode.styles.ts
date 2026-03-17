import styled from 'styled-components';

export const Container = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
`;

export const QRWrapper = styled.div`
  padding: 1rem;
  background: white;
  border-radius: 0.5rem;
  border: 1px solid ${({ theme }) => theme.gray[300]};
`;

export const SpaceName = styled.p`
  font-size: 0.875rem;
  color: ${({ theme }) => theme.gray[500]};
  text-align: center;
`;

export const ButtonWrapper = styled.div`
  display: flex;
  gap: 0.5rem;
`;
