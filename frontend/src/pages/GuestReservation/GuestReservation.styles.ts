import styled from 'styled-components';
import { Color } from 'types/common';

interface ColorDotProps {
  color: Color;
}

export const Section = styled.section`
  margin: 1.5rem 0 4.5rem;
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

export const ColorDot = styled.span<ColorDotProps>`
  display: inline-block;
  width: 1rem;
  height: 1rem;
  background-color: ${({ color }) => color};
  border-radius: 50%;
  margin-right: 0.75rem;
`;
