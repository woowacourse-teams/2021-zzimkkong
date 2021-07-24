import { Link } from 'react-router-dom';
import styled from 'styled-components';

export const Container = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
`;

export const PageHeader = styled.h2`
  font-size: 1.5rem;
  font-weight: 700;
  margin: 0.75rem 0 1.25rem;
`;

export const Image = styled.img``;

export const HomeLink = styled(Link)`
  font-size: 1.25rem;
  text-decoration: none;
  color: ${({ theme }) => theme.primary[400]};
`;
