import styled from 'styled-components';

export const ReservationForm = styled.form`
  margin: 1.5rem 0 5rem 0;
`;

export const Section = styled.section`
  margin: 1.5rem 0;
`;

export const PageHeader = styled.h2`
  font-size: 1.625rem;
  font-weight: 700;
  margin: 1.5rem 0;
`;

export const InputWrapper = styled.div`
  display: flex;
  gap: 1rem;

  label {
    flex: 1;
  }
`;

export const ReservationList = styled.div`
  border: 1px solid ${({ theme }) => theme.black[400]};

  [role='listitem'] {
    border-bottom: 1px solid ${({ theme }) => theme.black[400]};

    &:last-of-type {
      border-bottom: none;
    }
  }
`;

export const ButtonWrapper = styled.div`
  position: fixed;
  width: 100%;
  bottom: 0;
  left: 0;
`;
