import styled from 'styled-components';
import Button from 'components/Button/Button';
import ColorDotComponent from 'components/ColorDot/ColorDot';

export const ReservationContainer = styled.div`
  padding: 0 2rem 2rem;
`;

export const ReservationList = styled.div`
  overflow-y: auto;
  & > [role='listitem'] {
    border-bottom: 1px solid ${({ theme }) => theme.black[400]};

    &:last-of-type {
      border: 0;
    }
  }
`;

export const ReservationButton = styled(Button)`
  position: sticky;
  bottom: 0;
  left: 0;

  &:disabled {
    background-color: ${({ theme }) => theme.gray[400]};
    color: ${({ theme }) => theme.gray[300]};
  }
`;

export const SpaceTitle = styled.h3`
  position: sticky;
  top: 0;
  font-size: 1.25rem;
  text-align: center;
  padding: 2rem;
  background-color: ${({ theme }) => theme.white};
`;

export const PastDateMessage = styled.p`
  margin-top: 0.5rem;
  font-size: 1rem;
  color: ${({ theme }) => theme.gray[400]};
  text-align: center;
`;

export const ColorDot = styled(ColorDotComponent)`
  margin-right: 0.75rem;
`;

export const Message = styled.p`
  padding: 1rem 0;
`;

export const IconButtonWrapper = styled.div`
  display: flex;
  gap: 0.5rem;
`;
