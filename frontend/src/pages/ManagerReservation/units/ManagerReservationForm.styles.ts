import styled from 'styled-components';
import { Z_INDEX } from 'constants/style';

export const ReservationForm = styled.form`
  margin-top: 1.5rem;
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

export const ButtonWrapper = styled.div`
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100vw;
  z-index: ${Z_INDEX.RESERVATION_BUTTON};
`;

export const SettingSummaryWrapper = styled.div`
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  margin-top: 0.5rem;
`;

export const SettingSummary = styled.p<{ fontWeight?: string }>`
  white-space: pre-line;
  line-height: normal;
  font-size: 0.75rem;
  color: ${({ theme }) => theme.gray[500]};
  ${({ fontWeight }) => fontWeight && `font-weight: ${fontWeight}`};
`;
