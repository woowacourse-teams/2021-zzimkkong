import { useContext } from 'react';
import githubLogo from 'assets/images/github-logo.png';
import mapEditor from 'assets/images/map-editor.png';
import reservationPage from 'assets/images/reservation-page.png';
import spaceEditor from 'assets/images/space-editor.png';
import youtubeLogo from 'assets/images/youtube-logo.png';
import { ReactComponent as LogoIcon } from 'assets/svg/logo.svg';
import Header from 'components/Header/Header';
import Layout from 'components/Layout/Layout';
import PATH from 'constants/path';
import { AccessTokenContext } from 'providers/AccessTokenProvider';
import * as Styled from './Main.styles';
import { teamMembers } from './data';

const Main = (): JSX.Element => {
  const { accessToken } = useContext(AccessTokenContext);

  return (
    <>
      <Header />
      <Layout>
        <Styled.HiddenSectionTitle>서비스 소개</Styled.HiddenSectionTitle>
        <Styled.IntroductionContainer>
          <Styled.LogoContainer>
            <LogoIcon width="100%" height="100%" />
          </Styled.LogoContainer>
          <Styled.IntroductionTextContainer>
            <Styled.SubTitle>
              <Styled.PrimaryText>공간</Styled.PrimaryText>을 한 눈에,
              <Styled.PrimaryText> 예약</Styled.PrimaryText>은 한 번에!
            </Styled.SubTitle>
            <Styled.Description>
              공간 예약 시스템 제작 플랫폼 '찜꽁'은 맵 & 공간 에디터를 이용하여 누구나 쉽고 빠르게
              예약 시스템을 제작할 수 있습니다.
            </Styled.Description>
            <Styled.StartButton to={accessToken ? PATH.GUEST_MAIN : PATH.LOGIN}>
              시작하기
            </Styled.StartButton>
          </Styled.IntroductionTextContainer>
        </Styled.IntroductionContainer>
      </Layout>
      <Styled.InstructionsContainer>
        <Layout>
          <Styled.SectionTitle>이용 방법</Styled.SectionTitle>
          <Styled.InstructionsList>
            <Styled.InstructionsListItem>
              <Styled.InstructionsImage src={mapEditor} alt="맵 편집 페이지" />
              <Styled.InstructionsText instructionOrder={1}>
                예약받을 공간의 평면도 형태로 맵을 만들어 주세요.
              </Styled.InstructionsText>
            </Styled.InstructionsListItem>
            <Styled.InstructionsListItem>
              <Styled.InstructionsImage src={spaceEditor} alt="예약 공간 편집 페이지" />

              <Styled.InstructionsText instructionOrder={2}>
                예약 가능한 공간을 설정해주세요.
              </Styled.InstructionsText>
            </Styled.InstructionsListItem>
            <Styled.InstructionsListItem>
              <Styled.InstructionsImage src={reservationPage} alt="예약 페이지" />
              <Styled.InstructionsText instructionOrder={3}>
                공유된 링크로 접속하면 예약자가 공간을 예약할 수 있습니다!
              </Styled.InstructionsText>
            </Styled.InstructionsListItem>
          </Styled.InstructionsList>
        </Layout>
      </Styled.InstructionsContainer>
      <Styled.TeamMemberContainer>
        <Layout>
          <Styled.SectionTitle>팀원 소개</Styled.SectionTitle>
          <Styled.TeamMemberList>
            {teamMembers.map(({ image, name, position }) => (
              <Styled.TeamMemberListItem key={name}>
                <Styled.TeamMemberImage src={image} alt={name} />
                <Styled.TeamMemberName>
                  {name}({position})
                </Styled.TeamMemberName>
              </Styled.TeamMemberListItem>
            ))}
          </Styled.TeamMemberList>
        </Layout>
      </Styled.TeamMemberContainer>
      <Styled.Footer>
        <Layout>
          <Styled.Description>
            공간을 한 눈에, 예약은 한 번에!
            <br />
            공간 예약 시스템 제작 플랫폼, 찜꽁입니다.
          </Styled.Description>
          <Styled.Description>
            Contact : sunnyk5780@gmail.com
            <br />© 2021 zzimkkong.
          </Styled.Description>
          <Styled.FooterLinkList>
            <Styled.FooterLinkListItem>
              <Styled.FooterLink
                href="https://github.com/woowacourse-teams/2021-zzimkkong"
                target="_blank"
                rel="noopener noreferrer"
              >
                <Styled.FooterLinkImage src={githubLogo} alt="Github Logo" />
              </Styled.FooterLink>
            </Styled.FooterLinkListItem>
            <Styled.FooterLinkListItem>
              <Styled.FooterLink
                href="https://youtube.com/playlist?list=PLiXTzYqG2FYpng-iac1o4ZEh2pdV3hJnK"
                target="_blank"
                rel="noopener noreferrer"
              >
                <Styled.FooterLinkImage src={youtubeLogo} alt="Youtube Logo" />
              </Styled.FooterLink>
            </Styled.FooterLinkListItem>
          </Styled.FooterLinkList>
        </Layout>
      </Styled.Footer>
    </>
  );
};

export default Main;
