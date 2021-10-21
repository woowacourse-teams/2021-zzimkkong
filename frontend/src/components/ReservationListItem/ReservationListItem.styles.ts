import styled, { css } from 'styled-components';

interface StatusProps {
  status?: 'using' | 'done';
}

export const Item = styled.div`
  padding: 0.75rem 0;
  display: flex;
  gap: 1rem;
  border-bottom: 1px solid ${({ theme }) => theme.gray[400]};

  &:last-of-type {
    border-bottom: none;
  }
`;

const infoStyles = {
  using: null,
  done: css`
    color: ${({ theme }) => theme.gray[400]};
  `,
};

export const InfoWrapper = styled.div<StatusProps>`
  flex: 1;
  ${({ status }) => status && infoStyles[status]};
`;

export const Info = styled.div`
  padding-left: 0.25rem;
`;

export const Control = styled.div``;

export const Name = styled.p`
  font-size: 1rem;
  line-height: 1.75rem;
  display: flex;
  gap: 1rem;
`;

export const Description = styled.p<StatusProps>`
  font-size: 0.875rem;
  line-height: 1.375rem;
  color: ${({ theme }) => theme.black[100]};
  ${({ status }) => status && infoStyles[status]};
`;

export const Time = styled.p`
  font-size: 0.875rem;
  line-height: 1.375rem;
`;

const badgeStyles = {
  using: css`
    border-color: ${({ theme }) => theme.primary[500]};
    color: ${({ theme }) => theme.primary[500]};
  `,
  done: css`
    border-color: ${({ theme }) => theme.gray[400]};
    color: ${({ theme }) => theme.gray[400]};
  `,
};

export const StatusBadge = styled.span<StatusProps>`
  display: inline-block;
  padding: 0.25rem 0.375rem;
  border-width: 1px;
  border-style: solid;
  border-radius: 1rem;
  font-weight: bold;
  font-size: 0.75rem;
  ${({ status }) => status && badgeStyles[status]};
`;
