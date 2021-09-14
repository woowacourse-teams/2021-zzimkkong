import styled from 'styled-components';
import ColorDotComponent from 'components/ColorDot/ColorDot';

export const ReservationForm = styled.form`
  margin: 1.5rem 0 5rem 0;
`;

export const PageHeader = styled.h2`
  font-size: 1.625rem;
  font-weight: 700;
  margin: 1.5rem 0;
`;

export const Section = styled.section`
  margin: 1.5rem 0;
`;

export const InputWrapper = styled.div`
  position: relative;
  display: flex;
  gap: 1rem;
  margin: 1.625rem 0;

  label {
    flex: 1;
  }
`;

export const ReservationList = styled.div`
  border-top: 1px solid ${({ theme }) => theme.gray[400]};
`;

export const ButtonWrapper = styled.div`
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100vw;
`;

export const TimeFormMessage = styled.p`
  position: absolute;
  left: 0.75rem;
  bottom: -1rem;
  font-size: 0.75rem;
  height: 1em;
  color: ${({ theme }) => theme.gray[500]};
`;

export const Message = styled.p``;

export const ColorDot = styled(ColorDotComponent)`
  margin-right: 0.75rem;
`;
