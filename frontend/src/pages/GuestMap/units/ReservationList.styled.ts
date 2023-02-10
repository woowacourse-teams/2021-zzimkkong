import styled from 'styled-components';
import ColorDotComponent from 'components/ColorDot/ColorDot';

export const ReservationListWrapper = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1rem;
`;

export const SpaceTitle = styled.h3`
  position: sticky;
  top: 0;
  font-size: 1.25rem;
  text-align: center;
  background-color: ${({ theme }) => theme.white};
`;

export const ColorDot = styled(ColorDotComponent)`
  margin-right: 0.75rem;
`;
