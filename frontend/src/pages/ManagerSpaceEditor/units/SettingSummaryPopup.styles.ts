import styled from 'styled-components';

export const SettingSummaryPopupWrapper = styled.div`
  padding: 2rem 3rem;
`;

export const SettingSummary = styled.p<{ fontWeight?: string }>`
  white-space: pre-line;
  line-height: normal;
  font-size: 0.75rem;
  color: ${({ theme }) => theme.gray[500]};
  ${({ fontWeight }) => fontWeight && `font-weight: ${fontWeight}`};
`;
