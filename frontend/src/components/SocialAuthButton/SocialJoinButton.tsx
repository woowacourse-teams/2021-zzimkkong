import { ButtonHTMLAttributes } from 'react';
import { ReactComponent as GithubIcon } from 'assets/svg/github-logo.svg';
import { ReactComponent as GoogleIcon } from 'assets/svg/google-logo.svg';
import * as Styled from './SocialAuthButton.styles';

export interface Props extends ButtonHTMLAttributes<HTMLButtonElement> {
  provider: 'GITHUB' | 'GOOGLE';
}

const social = {
  GITHUB: {
    icon: <GithubIcon />,
    text: 'Github로 시작하기',
  },
  GOOGLE: {
    icon: <GoogleIcon />,
    text: 'Google로 시작하기',
  },
};

const SocialJoinButton = ({ provider, ...props }: Props): JSX.Element => {
  return (
    <Styled.SocialJoinButton provider={provider} {...props}>
      <Styled.Icon>{social[provider].icon}</Styled.Icon>
      <Styled.Text>{social[provider].text}</Styled.Text>
    </Styled.SocialJoinButton>
  );
};

export default SocialJoinButton;
