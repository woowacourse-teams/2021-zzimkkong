import { Story } from '@storybook/react';
import { PropsWithChildren } from 'react';
import SocialJoinButton, { Props } from './SocialJoinButton';

export default {
  title: 'shared/SocialJoinButton',
  component: SocialJoinButton,
  argTypes: {
    provider: {
      options: ['GITHUB', 'GOOGLE'],
      control: { type: 'radio' },
    },
  },
};

const Template: Story<PropsWithChildren<Props>> = (args) => <SocialJoinButton {...args} />;

export const GithubJoin = Template.bind({});
GithubJoin.args = {
  provider: 'GITHUB',
};

export const GoogleJoin = Template.bind({});
GoogleJoin.args = {
  provider: 'GOOGLE',
};
