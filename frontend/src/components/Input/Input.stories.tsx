import { Story } from '@storybook/react';
import Input, { Props } from './Input';
import { ReactComponent as LogoIcon } from 'assets/svg/logo.svg';

export default {
  title: 'shared/Input',
  component: Input,
};

const Template: Story<Props> = (args) => <Input {...args} />;

export const Default = Template.bind({});
Default.args = {
  label: '이메일',
};

export const Icon = Template.bind({});
Icon.args = {
  icon: <LogoIcon />,
  label: '이메일',
};
