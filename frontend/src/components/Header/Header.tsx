import * as Styled from './Header.styles';
import { ReactComponent as LogoIcon } from 'assets/svg/logo.svg';

const Header = (): JSX.Element => (
  <Styled.Header>
    <LogoIcon />
    <Styled.Title>찜꽁</Styled.Title>
  </Styled.Header>
);

export default Header;
