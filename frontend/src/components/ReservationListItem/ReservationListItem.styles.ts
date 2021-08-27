import styled from 'styled-components';

export const Item = styled.div`
  padding: 0.75rem 0;
  display: flex;
  gap: 1rem;
  border-bottom: 1px solid ${({ theme }) => theme.gray[400]};

  &:last-of-type {
    border-bottom: none;
  }
`;

export const Info = styled.div`
  flex: 1;
`;

export const Control = styled.div``;

export const Name = styled.p`
  font-size: 1rem;
  line-height: 1.75rem;
`;

export const Description = styled.p`
  font-size: 0.875rem;
  line-height: 1.375rem;
  color: ${({ theme }) => theme.black[100]};
`;

export const Time = styled.p`
  font-size: 0.875rem;
  line-height: 1.375rem;
`;
