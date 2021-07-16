import { TdHTMLAttributes, ThHTMLAttributes } from 'react';
import styled, { css } from 'styled-components';

interface TableCellProps {
  wordWrap?: boolean;
  align?: 'left' | 'center' | 'right' | 'justify';
}

interface TableHeaderCellProps
  extends Omit<ThHTMLAttributes<HTMLTableHeaderCellElement>, 'align'>,
    TableCellProps {}

interface TableDataCellProps
  extends Omit<TdHTMLAttributes<HTMLTableDataCellElement>, 'align'>,
    TableCellProps {}

const wordWrapCSS = css<TableCellProps>`
  ${({ wordWrap }) => (wordWrap ? `white-space: nowrap; word-break: keep-all;` : '')};
`;

const alignCSS = css<TableCellProps>`
  ${({ align = 'center' }) => `text-align: ${align}`};
`;

export const Table = styled.table`
  font-size: 1rem;
  table-layout: fixed;
  border-collapse: collapse;
  border-spacing: 0;
  table-layout: auto;
  width: 100%;
`;

export const TableRow = styled.tr`
  border-bottom: 1px solid ${({ theme }) => theme.black[400]};

  &:last-of-type {
    border-bottom: none;
  }
`;

export const TableHead = styled.thead``;

export const TableHeader = styled.th<TableHeaderCellProps>`
  padding: 1rem;
  word-break: break-word;
  vertical-align: middle;
  font-weight: 700;
  border-bottom: 1px solid ${({ theme }) => theme.black[400]};
  ${wordWrapCSS};
  ${alignCSS};
`;

export const TableBody = styled.tbody``;

export const TableCell = styled.td<TableDataCellProps>`
  padding: 1rem;
  word-break: break-word;
  vertical-align: middle;
  ${wordWrapCSS};
  ${alignCSS};
`;
