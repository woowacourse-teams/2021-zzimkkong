import { Link } from 'react-router-dom';
import styled, { css } from 'styled-components';

const screenReaderOnlyCSS = css`
  width: 0;
  height: 0;
  overflow: hidden;
`;

export const PrimaryText = styled.span`
  color: ${({ theme }) => theme.primary[400]};
`;

export const HiddenSectionTitle = styled.h2`
  ${screenReaderOnlyCSS}
`;

export const SectionTitle = styled.h2`
  font-size: 1.375rem;
  font-weight: 700;
  color: ${({ theme }) => theme.gray[500]};
  text-align: center;
  margin-bottom: 2.5rem;
`;

export const IntroductionContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 7rem auto;
  padding: 0 1rem;
  max-width: ${({ theme: { breakpoints } }) => breakpoints.md}px;

  @media (max-width: ${({ theme: { breakpoints } }) => breakpoints.md}px) {
    flex-direction: column;
    margin: 5rem auto;
  }
`;

export const LogoContainer = styled.div`
  flex-grow: 1;
`;

export const IntroductionTextContainer = styled.div`
  margin-left: 1.25rem;
  flex-grow: 2;

  @media (max-width: ${({ theme: { breakpoints } }) => breakpoints.md}px) {
    margin: 2rem 0 0;
  }
`;

export const SubTitle = styled.h3`
  font-size: 1.5rem;
  font-weight: 700;
  margin-bottom: 1rem;

  @media (max-width: ${({ theme: { breakpoints } }) => breakpoints.md}px) {
    font-size: 1.25rem;
  }
`;

export const Description = styled.p`
  font-size: 0.875rem;
  line-height: 1.375rem;
  color: ${({ theme }) => theme.gray[500]};
  margin-bottom: 0.5rem;

  &:last-child {
    margin-bottom: 0;
  }
`;

export const StartButton = styled(Link)`
  padding: 0.75rem 1.5rem;
  margin-top: 1.75rem;
  border-radius: 0.125rem;
  color: ${({ theme }) => theme.white};
  background-color: ${({ theme }) => theme.primary[400]};
  text-decoration: none;
  display: block;
  text-align: center;

  &:hover {
    background-color: ${({ theme }) => theme.primary[500]};
  }
`;

export const InstructionsContainer = styled.div`
  background-color: ${({ theme }) => theme.gray[100]};
  padding: 6rem 0 5.75rem;
`;

export const InstructionsImage = styled.img`
  width: 100%;
  margin: 0 auto 1.5rem;
  border: 1px solid ${({ theme }) => theme.gray[300]};
`;

export const InstructionsList = styled.ol`
  max-width: ${({ theme: { breakpoints } }) => breakpoints.lg}px;
  display: flex;
  margin: 0 auto;

  @media (max-width: ${({ theme: { breakpoints } }) => breakpoints.sm}px) {
    flex-direction: column;
    align-items: center;
  }
`;

export const InstructionsListItem = styled.li`
  width: 33.333%;
  padding: 0 2rem;
  line-height: 1.375rem;

  @media (max-width: ${({ theme: { breakpoints } }) => breakpoints.sm}px) {
    width: 100%;
    padding: 0 1rem;
    margin-bottom: 2rem;

    &:last-child {
      margin-bottom: 0;
    }
  }
`;

export const TeamMemberContainer = styled.div`
  margin: 7rem 0;
`;

export const TeamMemberList = styled.ul`
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  margin: 0 2rem;

  @media (max-width: ${({ theme: { breakpoints } }) => breakpoints.sm}px) {
    margin: 0 1rem;
  }
`;

export const TeamMemberListItem = styled.li`
  width: 25%;
  display: flex;
  flex-direction: column;
  align-items: center;

  @media (max-width: ${({ theme: { breakpoints } }) => breakpoints.sm}px) {
    width: 50%;
  }
`;

export const TeamMemberImage = styled.img`
  background-color: ${({ theme }) => theme.gray[100]};
  border-radius: 50%;
  width: 80%;
  max-width: 150px;
`;

export const TeamMemberName = styled.p`
  text-align: center;
  margin: 0.75rem 0 2rem;
`;

export const Footer = styled.footer`
  border-top: 1px solid ${({ theme }) => theme.gray[200]};
  padding: 3rem 1rem;
`;

export const FooterLinkList = styled.ul`
  display: flex;
  margin-top: 1rem;
`;

export const FooterLinkListItem = styled.li`
  margin-right: 1rem;
`;

export const FooterLinkImage = styled.img`
  opacity: 0.5;
`;

export const FooterLink = styled.a`
  &:hover {
    ${FooterLinkImage} {
      opacity: 1;
    }
  }
`;
