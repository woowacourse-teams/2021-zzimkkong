import { Link, useParams } from 'react-router-dom';
import { ReactComponent as LogoIcon } from 'assets/svg/logo.svg';
import PATH from 'constants/path';
import * as Styled from './Header.styles';

const Header = (): JSX.Element => {
  const params = useParams<{ publicMapId: string }>();
  const publicMapId = params?.publicMapId;

  return (
    <Styled.Header>
      <Styled.HeaderLayout>
        <Styled.HeaderLink to={publicMapId ? `/guest/${publicMapId}` : PATH.MANAGER_MAIN}>
          <Styled.Logo>
            <LogoIcon />
          </Styled.Logo>
          <Styled.Title>찜꽁</Styled.Title>
        </Styled.HeaderLink>
      </Styled.HeaderLayout>
    </Styled.Header>
  );
};

export default Header;
