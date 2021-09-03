import { useHistory, useParams } from 'react-router-dom';
import { useRecoilState } from 'recoil';
import { queryClient } from 'App';
import { ReactComponent as LogoIcon } from 'assets/svg/logo.svg';
import PATH, { HREF } from 'constants/path';
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

  const getHeaderLinkPath = () => {
    if (sharingMapId) return HREF.GUEST_MAP(sharingMapId);

    if (accessToken) return PATH.MANAGER_MAIN;

    return PATH.MAIN;
  };

  const handleLogout = () => {
    setAccessToken('');
    queryClient.clear();
    localStorage.removeItem(LOCAL_STORAGE_KEY.ACCESS_TOKEN);

    history.push(PATH.MANAGER_LOGIN);
  };

  return (
    <Styled.Header>
      <Styled.HeaderLayout>
        <Styled.HeaderLink to={getHeaderLinkPath()}>
          <Styled.Logo>
            <LogoIcon />
          </Styled.Logo>
          <Styled.Title>찜꽁</Styled.Title>
        </Styled.HeaderLink>
        {!sharingMapId && (
          <Styled.ButtonContainer>
            {accessToken ? (
              <Styled.TextButton variant="text" onClick={handleLogout}>
                로그아웃
              </Styled.TextButton>
            ) : (
              <>
                <Styled.TextLink to={PATH.MANAGER_LOGIN}>로그인</Styled.TextLink>
                <Styled.TextLink to={PATH.MANAGER_JOIN}>회원가입</Styled.TextLink>
              </>
            )}
          </Styled.ButtonContainer>
        )}
      </Styled.HeaderLayout>
    </Styled.Header>
  );
};

export default Header;
