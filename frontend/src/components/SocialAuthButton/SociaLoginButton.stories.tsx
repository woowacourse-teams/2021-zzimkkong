import { Story } from '@storybook/react';
import { PropsWithChildren } from 'react';
import SocialLoginButton, { Props } from './SocialLoginButton';

export default {
  title: 'shared/SocialLoginButton',
  component: SocialLoginButton,
  argTypes: {
    provider: {
      options: ['GITHUB', 'GOOGLE'],
      control: { type: 'radio' },
    },
  },
};

const Template: Story<PropsWithChildren<Props>> = (args) => <SocialLoginButton {...args} />;

export const GithubLogin = Template.bind({});
GithubLogin.args = {
  provider: 'GITHUB',
};

export const GoogleLogin = Template.bind({});
GoogleLogin.args = {
  provider: 'GOOGLE',
};

export const GithubJoin = Template.bind({});
GithubJoin.args = {
  provider: 'GITHUB',
};

export const GoogleJoin = Template.bind({});
GoogleJoin.args = {
  provider: 'GOOGLE',
};
