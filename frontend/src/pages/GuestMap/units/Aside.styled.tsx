import styled from 'styled-components';
import { Z_INDEX } from 'constants/style';

export const Container = styled.aside`
  width: 30%;
  padding: 1.5rem 1rem;
  min-width: ${({ theme }) => theme.breakpoints.sm}px;
  box-shadow: 0.25rem 0 0.25rem 0 ${({ theme }) => theme.shadow};
  z-index: ${Z_INDEX.ASIDE};
`;

export const Notice = styled.div`
  border: 1px solid ${({ theme }) => theme.gray[300]};
  border-radius: 0.5rem;
  padding: 0.5rem 1rem;
`;

export const NoticeTitle = styled.span`
  color: ${({ theme }) => theme.primary[500]};
  font-weight: bold;
  display: inline-block;
  line-height: 1.25rem;
  margin-bottom: 0.5rem;
`;

export const NoticeText = styled.p`
  flex: 1;
  color: ${({ theme }) => theme.gray[500]};
  font-size: 0.875rem;
  line-height: 1.25rem;
`;

export const SwitchContainer = styled.div`
  margin: 2rem 0;
  text-align: center;
`;
