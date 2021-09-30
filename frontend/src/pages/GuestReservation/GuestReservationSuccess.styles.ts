import { Link } from 'react-router-dom';
import styled from 'styled-components';

export const Container = styled.div`
  text-align: center;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 1.5rem;
  height: calc(100vh - 4rem);
  padding-bottom: 6rem;

  svg {
    max-width: 200px;
  }
`;

export const Message = styled.p`
  font-size: 1.5rem;
`;

export const PageLink = styled(Link)`
  color: ${({ theme }) => theme.primary[500]};
  text-decoration: none;
`;

export const Logo = styled.img``;
