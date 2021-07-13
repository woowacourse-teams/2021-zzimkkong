import { ReactComponent as LogoIcon } from 'assets/svg/logo.svg';
import * as Styled from './Header.styles';

const Header = (): JSX.Element => (
  <Styled.Header>
    <Styled.HeaderLayout>
      <LogoIcon />
      <Styled.Title>찜꽁</Styled.Title>
    </Styled.HeaderLayout>
  </Styled.Header>
);

export default Header;
