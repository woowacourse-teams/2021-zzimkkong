import styled, { css } from 'styled-components';

export const Container = styled.div`
  width: 100%;
  display: flex;
  flex-direction: column;
`;

export const TabItemContainer = styled.div`
  width: 100%;
  display: flex;
  justify-content: flex-end;
`;

const selectedCSS = {
  default: css`
    background-color: ${({ theme }) => theme.gray[100]};
    color: ${({ theme }) => theme.gray[500]};
  `,
  selected: css`
    background-color: ${({ theme }) => theme.white};
    color: ${({ theme }) => theme.primary[500]};
  `,
};

export const TabItem = styled.div<{ selected?: boolean }>`
  padding: 1.375rem 2.375rem;
  font-weight: bold;
  border-radius: 1.125rem 1.125rem 0 0;
  cursor: pointer;

  ${({ selected }) => (selected ? selectedCSS.selected : selectedCSS.default)}
`;
