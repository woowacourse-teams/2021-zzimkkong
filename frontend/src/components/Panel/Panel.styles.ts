import styled from 'styled-components';

export const Panel = styled.div`
  border: 1px solid ${({ theme }) => theme.black[400]};
`;

export const Title = styled.p`
  display: inline-block;
  font-weight: 700;
  font-size: 1.1rem;
`;

export const Inner = styled.div`
  padding: 0.8rem;
`;
