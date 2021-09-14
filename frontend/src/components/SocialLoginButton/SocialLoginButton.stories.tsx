import { Story } from '@storybook/react';
import { PropsWithChildren } from 'react';
import SocialLoginButton, { Props } from './SocialLoginButton';

export default {
  title: 'shared/SocialLoginButton',
  component: SocialLoginButton,
  argTypes: {
    provider: {
      options: ['github', 'google'],
      control: { type: 'radio' },
    },
  },
};

const Template: Story<PropsWithChildren<Props>> = (args) => <SocialLoginButton {...args} />;

export const Github = Template.bind({});
Github.args = {
  provider: 'github',
};

export const Google = Template.bind({});
Google.args = {
  provider: 'google',
};
