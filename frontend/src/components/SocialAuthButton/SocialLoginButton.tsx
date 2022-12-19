import { AnchorHTMLAttributes } from 'react';
import { ReactComponent as GithubIcon } from 'assets/svg/github-logo.svg';
import { ReactComponent as GoogleIcon } from 'assets/svg/google-logo.svg';
import * as Styled from './SocialAuthButton.styles';

export interface Props extends AnchorHTMLAttributes<HTMLAnchorElement> {
  provider: 'GITHUB' | 'GOOGLE';
  variant?: 'default' | 'icon';
}

const social = {
  GITHUB: {
    icon: <GithubIcon />,
    text: 'Github로 로그인',
  },
  GOOGLE: {
    icon: <GoogleIcon />,
    text: 'Google로 로그인',
  },
};

const SocialLoginButton = ({ provider, variant = 'default', ...props }: Props): JSX.Element => {
  return (
    <Styled.SocialLoginButton provider={provider} variant={variant} {...props}>
      <Styled.Icon>{social[provider].icon}</Styled.Icon>
      {variant === 'default' && <Styled.Text>{social[provider].text}</Styled.Text>}
    </Styled.SocialLoginButton>
  );
};

export default SocialLoginButton;
