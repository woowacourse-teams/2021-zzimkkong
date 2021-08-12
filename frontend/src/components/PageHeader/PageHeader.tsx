import { ReactNode } from 'react';
import * as Styled from './PageHeader.styles';

export interface Props {
  title: string;
  leftButtons?: ReactNode;
  rightButtons?: ReactNode;
}

const PageHeader = ({ title, leftButtons, rightButtons }: Props): JSX.Element => {
  return (
    <Styled.Container>
      {leftButtons && <Styled.ButtonContainer>{leftButtons}</Styled.ButtonContainer>}
      <Styled.Title hasLeftButton={leftButtons !== undefined}>{title}</Styled.Title>
      {rightButtons && <Styled.ButtonContainer>{rightButtons}</Styled.ButtonContainer>}
    </Styled.Container>
  );
};

export default PageHeader;
