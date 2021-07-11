import { PropsWithChildren, TableHTMLAttributes } from 'react';
import * as Styled from './Table.styles';

export interface Props extends TableHTMLAttributes<HTMLTableElement> {}

const Table = ({ children }: PropsWithChildren<Props>) => {
  return <Styled.Table>{children}</Styled.Table>;
};

Table.Row = Styled.TableRow;
Table.Head = Styled.TableHead;
Table.Header = Styled.TableHeader;
Table.Body = Styled.TableBody;
Table.Cell = Styled.TableCell;

export default Table;
