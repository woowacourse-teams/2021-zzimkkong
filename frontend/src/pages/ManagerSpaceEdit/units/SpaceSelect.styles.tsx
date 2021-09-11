import styled from 'styled-components';
import { Z_INDEX } from 'constants/style';

export const SpaceSelect = styled.div`
  border-bottom: 1px solid ${({ theme }) => theme.gray[300]};
  padding: 1.5rem;
  position: relative;
`;

export const FormHeader = styled.h3`
  font-size: 1.5rem;
  margin-bottom: 1.625rem;
`;

export const SpaceSelectWrapper = styled.div`
  margin-bottom: 0.5rem;
`;

export const SpaceOption = styled.div`
  display: flex;
  align-items: center;
  gap: 0.75rem;
`;
