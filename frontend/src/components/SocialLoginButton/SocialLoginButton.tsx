import { AnchorHTMLAttributes } from 'react';
import { ReactComponent as GithubIcon } from 'assets/svg/github-logo.svg';
import { ReactComponent as GoogleIcon } from 'assets/svg/google-logo.svg';
import * as Styled from './SocialLoginButton.styles';

export interface Props extends AnchorHTMLAttributes<HTMLAnchorElement> {
  provider: 'github' | 'google';
}

const social = {
  github: {
    icon: <GithubIcon />,
    text: 'Github로 로그인',
  },
  google: {
    icon: <GoogleIcon />,
    text: 'Google로 로그인',
  },
};

const SocialLoginButton = ({ provider, ...props }: Props): JSX.Element => {
  return (
    <Styled.SocialLoginButton provider={provider} {...props}>
      <Styled.Icon>{social[provider].icon}</Styled.Icon>
      <Styled.Text>{social[provider].text}</Styled.Text>
    </Styled.SocialLoginButton>
  );
};

export default SocialLoginButton;
