import { Link } from 'react-router-dom';
import styled, { css } from 'styled-components';
import ColorDotComponent from 'components/ColorDot/ColorDot';
import { FORM_MAX_WIDTH } from 'constants/style';

export const Container = styled.div`
  max-width: ${FORM_MAX_WIDTH};
  margin: 0 auto;
  padding-bottom: 4rem;
`;

export const SpaceHeader = styled.div`
  text-align: center;
  margin: 2rem 0 1.5rem;
`;

export const MapName = styled.p`
  font-size: 0.875rem;
  color: ${({ theme }) => theme.gray[500]};
  margin-bottom: 0.5rem;
`;

export const SpaceNameWrapper = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
`;

export const SpaceName = styled.h2`
  font-size: 1.625rem;
  font-weight: 700;
`;

export const ColorDot = styled(ColorDotComponent)`
  flex-shrink: 0;
`;

export const StatusBadge = styled.span<{ available: boolean }>`
  display: inline-block;
  padding: 0.25rem 0.75rem;
  border-radius: 1rem;
  font-size: 0.875rem;
  font-weight: 600;
  margin-top: 0.75rem;

  ${({ available, theme }) =>
    available
      ? css`
          background-color: ${theme.primary[100]};
          color: ${theme.primary[400]};
        `
      : css`
          background-color: ${theme.gray[200]};
          color: ${theme.gray[500]};
        `}
`;

export const ReserveButtonWrapper = styled.div`
  margin: 1.5rem 0;
`;

export const Section = styled.section`
  margin: 1.5rem 0;
`;

export const SectionTitle = styled.h3`
  font-size: 1.125rem;
  font-weight: 700;
  margin-bottom: 1rem;
  padding-bottom: 0.5rem;
  border-bottom: 2px solid ${({ theme }) => theme.primary[400]};
`;

export const ReservationList = styled.div`
  border-top: 1px solid ${({ theme }) => theme.gray[400]};
`;

export const Message = styled.p`
  white-space: pre-wrap;
  color: ${({ theme }) => theme.gray[500]};
  text-align: center;
  padding: 2rem 0;
`;

export const SettingInfo = styled.div`
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  margin-bottom: 1rem;
`;

export const SettingText = styled.p`
  font-size: 0.875rem;
  color: ${({ theme }) => theme.gray[500]};
`;

export const LoadingContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 50vh;
`;

export const LoadingMessage = styled.p`
  font-size: 1.125rem;
  color: ${({ theme }) => theme.gray[500]};
  margin-top: 1rem;
`;

export const ErrorContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 50vh;
  text-align: center;
`;

export const ErrorMessage = styled.p`
  font-size: 1.125rem;
  color: ${({ theme }) => theme.gray[500]};
  margin-bottom: 1.5rem;
  white-space: pre-wrap;
`;

export const HomeLink = styled(Link)`
  color: ${({ theme }) => theme.primary[400]};
  text-decoration: underline;
  font-size: 1rem;
`;

export const MapLink = styled(Link)`
  display: inline-block;
  color: ${({ theme }) => theme.primary[400]};
  font-size: 0.875rem;
  margin-top: 0.5rem;
  text-decoration: underline;
`;
