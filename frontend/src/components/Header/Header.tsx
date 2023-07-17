import { useContext } from 'react';
import { useHistory, useLocation, useParams } from 'react-router-dom';
import { queryClient } from 'App';
import { ReactComponent as LogoIcon } from 'assets/svg/logo.svg';
import PATH, { HREF } from 'constants/path';
import { AccessTokenContext } from 'providers/AccessTokenProvider';
import { MapItem } from 'types/common';
import * as Styled from './Header.styles';

interface Params {
  sharingMapId?: MapItem['sharingMapId'];
}

interface HeaderProps {
  onClickLogin?: () => void;
}

const Header = ({ onClickLogin }: HeaderProps): JSX.Element => {
  const location = useLocation();
  const history = useHistory();
  const { accessToken, resetAccessToken } = useContext(AccessTokenContext);

  const params = useParams<Params>();
  const sharingMapId = params?.sharingMapId;

  const getHeaderLinkPath = () => {
    if (!accessToken && sharingMapId) return HREF.GUEST_MAP(sharingMapId);

    if (!accessToken) return PATH.MAIN;

    if (location.pathname.includes('/guest')) return PATH.GUEST_MAIN;

    return PATH.MANAGER_MAP_LIST;
  };

  const handleLogout = () => {
    resetAccessToken();
    queryClient.clear();

    if (sharingMapId) {
      history.push(HREF.GUEST_MAP(sharingMapId));

      return;
    }

    history.push(PATH.LOGIN);
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
        <Styled.ButtonContainer>
          {accessToken ? (
            <>
              <Styled.TextLink to={PATH.MANAGER_MAP_LIST}>마이 페이지</Styled.TextLink>
              <Styled.TextButton variant="text" onClick={handleLogout}>
                로그아웃
              </Styled.TextButton>
            </>
          ) : (
            <>
              <Styled.TextLink to={PATH.GUEST_NON_LOGIN_RESERVATION_SEARCH}>
                비회원 예약 조회
              </Styled.TextLink>
              <Styled.TextLink
                to={sharingMapId ? HREF.GUEST_MAP(sharingMapId) : PATH.LOGIN}
                onClick={onClickLogin}
              >
                로그인
              </Styled.TextLink>
              <Styled.TextLink to={PATH.MANAGER_JOIN}>회원가입</Styled.TextLink>
            </>
          )}
        </Styled.ButtonContainer>
      </Styled.HeaderLayout>
    </Styled.Header>
  );
};

export default Header;
