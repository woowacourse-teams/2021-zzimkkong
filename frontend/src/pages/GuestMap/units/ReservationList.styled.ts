import styled from 'styled-components';
import ColorDotComponent from 'components/ColorDot/ColorDot';

export const ReservationListWrapper = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1rem;
`;

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

export const Message = styled.p`
  padding: 1rem 0;
`;

export const IconButtonWrapper = styled.div`
  display: flex;
  gap: 0.5rem;
`;

export const MessageWrapper = styled.div`
  display: flex;
  justify-content: center;
`;

export const InputWrapper = styled.div`
  position: relative;
  display: flex;
  flex-direction: column;
  margin-bottom: 1.625rem;

  & > label,
  & > div {
    flex: 1;
  }
`;

export const SpaceOption = styled.div`
  display: flex;
  align-items: center;
  gap: 0.75rem;
`;
