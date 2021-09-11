import styled from 'styled-components';

export const SpaceSelect = styled.div`
  border-bottom: 1px solid ${({ theme }) => theme.gray[300]};
  padding: 1.5rem;
  position: relative;
`;

export const Title = styled.h3`
  font-size: 1.25rem;
  margin-bottom: 1.5rem;
`;

export const SpaceSelectWrapper = styled.div`
  margin-bottom: 0.5rem;
`;

export const SpaceOption = styled.div`
  display: flex;
  align-items: center;
  gap: 0.75rem;
`;
