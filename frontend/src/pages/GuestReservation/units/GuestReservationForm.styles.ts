import styled from 'styled-components';
import Button from 'components/Button/Button';
import { Z_INDEX } from 'constants/style';

export const ReservationForm = styled.form`
  margin: 1.5rem 0 0;
`;

export const Section = styled.section`
  margin: 1.5rem 0;
`;

export const InputWrapper = styled.div`
  position: relative;
  display: flex;
  flex-direction: column;
  margin: 1.625rem 0;

  & > label,
  & > div {
    flex: 1;
  }
`;

export const TimeFormMessageWrapper = styled.div`
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  margin-top: 0.5rem;
`;

export const TimeFormMessage = styled.p`
  left: 0.75rem;
  bottom: -1rem;
  font-size: 0.75rem;
  height: 1em;
  color: ${({ theme }) => theme.gray[500]};
`;

export const ButtonWrapper = styled.div`
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100vw;
  z-index: ${Z_INDEX.RESERVATION_BUTTON};
`;

export const ReservationButton = styled(Button)`
  &:disabled {
    background-color: ${({ theme }) => theme.gray[400]};
    color: ${({ theme }) => theme.gray[300]};
    cursor: not-allowed;
  }
`;
