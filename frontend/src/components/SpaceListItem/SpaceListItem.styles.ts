import styled from 'styled-components';

export const Container = styled.div`
  padding: 0.25rem;
  border: 1px solid ${({ theme }) => theme.black[400]};
`;

export const ImageWrapper = styled.div``;

export const Image = styled.img`
  width: 100%;
`;

export const Title = styled.h3`
  font-size: 0.875rem;
  padding: 0.375rem 0;
`;
