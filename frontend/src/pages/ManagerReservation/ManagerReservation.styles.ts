import styled from 'styled-components';
import ColorDotComponent from 'components/ColorDot/ColorDot';

interface Props {
  isEditMode: boolean;
}

export const Section = styled.section<Props>`
  margin-top: ${({ isEditMode }) => (isEditMode ? '3rem' : '1.5rem')};
  margin-bottom: 4.5rem;
`;

export const Message = styled.p`
  white-space: pre-wrap;
`;

export const ReservationList = styled.div`
  border-top: 1px solid ${({ theme }) => theme.gray[400]};
`;

export const PageHeader = styled.h2`
  font-size: 1.625rem;
  font-weight: 700;
  margin: 1.5rem 0;
  display: flex;
  align-items: center;
`;

export const ColorDot = styled(ColorDotComponent)`
  margin-right: 0.75rem;
`;
