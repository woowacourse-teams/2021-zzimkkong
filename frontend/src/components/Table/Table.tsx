import { PropsWithChildren, TableHTMLAttributes } from 'react';
import * as Styled from './Table.styles';

export type Props = TableHTMLAttributes<HTMLTableElement>;

const Table = ({ children }: PropsWithChildren<Props>): JSX.Element => (
  <Styled.Table>{children}</Styled.Table>
);

Table.Row = Styled.TableRow;
Table.Head = Styled.TableHead;
Table.Header = Styled.TableHeader;
Table.Body = Styled.TableBody;
Table.Cell = Styled.TableCell;

export default Table;
