import { Link } from 'react-router-dom';
import styled from 'styled-components';

export const Container = styled.div`
  text-align: center;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 2rem;
  height: calc(100vh - 4rem);

  svg {
    max-width: 200px;
    margin-bottom: 0.5rem;
  }
`;

export const Message = styled.p`
  font-size: 1.25rem;
  font-weight: bold;
`;

export const MessageContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1rem;
`;

export const Info = styled.div`
  text-align: left;
  color: ${({ theme }) => theme.gray[500]};
  padding: 1.25rem 1rem;
  border-top: 1px;
  border-style: solid;
  border-color: ${({ theme }) => theme.gray[300]};
`;

export const InfoRow = styled.p`
  margin: 0.75rem 0;
  display: flex;
  gap: 1rem;
`;

export const InfoLabel = styled.span`
  font-weight: bold;
`;

export const InfoText = styled.span`
  flex: 1;
`;

export const PageLink = styled(Link)`
  color: ${({ theme }) => theme.primary[500]};
  text-decoration: none;
`;

export const Logo = styled.img``;
