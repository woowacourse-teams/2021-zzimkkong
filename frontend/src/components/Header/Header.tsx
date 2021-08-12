import { useHistory, useParams } from 'react-router-dom';
import { useRecoilState } from 'recoil';
import { ReactComponent as LogoIcon } from 'assets/svg/logo.svg';
import PATH from 'constants/path';
import { LOCAL_STORAGE_KEY } from 'constants/storage';
import accessTokenState from 'state/accessTokenState';
import { MapItem } from 'types/common';
import * as Styled from './Header.styles';

interface Params {
  sharingMapId?: MapItem['sharingMapId'];
}

const Header = (): JSX.Element => {
  const [accessToken, setAccessToken] = useRecoilState(accessTokenState);

  const history = useHistory();

  const params = useParams<Params>();
  const sharingMapId = params?.sharingMapId;

  const handleLogout = () => {
    setAccessToken('');
    localStorage.removeItem(LOCAL_STORAGE_KEY.ACCESS_TOKEN);

    history.push(PATH.MANAGER_LOGIN);
  };

  return (
    <Styled.Header>
      <Styled.HeaderLayout>
        <Styled.HeaderLink to={sharingMapId ? `/guest/${sharingMapId}` : PATH.MANAGER_MAIN}>
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
