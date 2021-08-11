import { useHistory, useParams } from 'react-router-dom';
import { useRecoilState } from 'recoil';
import { ReactComponent as LogoIcon } from 'assets/svg/logo.svg';
import PATH from 'constants/path';
import { LOCAL_STORAGE_KEY } from 'constants/storage';
import accessTokenState from 'state/accessTokenState';
import * as Styled from './Header.styles';

interface Params {
  publicMapId?: string;
}

const Header = (): JSX.Element => {
  const [accessToken, setAccessToken] = useRecoilState(accessTokenState);

  const history = useHistory();

  const params = useParams<Params>();
  const publicMapId = params?.publicMapId;

  const handleLogout = () => {
    setAccessToken('');
    localStorage.removeItem(LOCAL_STORAGE_KEY.ACCESS_TOKEN);

    history.push(PATH.MANAGER_LOGIN);
  };

  return (
    <Styled.Header>
      <Styled.HeaderLayout>
        <Styled.HeaderLink to={publicMapId ? `/guest/${publicMapId}` : PATH.MANAGER_MAIN}>
          <Styled.Logo>
            <LogoIcon />
          </Styled.Logo>
          <Styled.Title>찜꽁</Styled.Title>
        </Styled.HeaderLink>
        {accessToken && (
          <Styled.TextButton variant="text" onClick={handleLogout}>
            로그아웃
          </Styled.TextButton>
        )}
      </Styled.HeaderLayout>
    </Styled.Header>
  );
};

export default Header;
