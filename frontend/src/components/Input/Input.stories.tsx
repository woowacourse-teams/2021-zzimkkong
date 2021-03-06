import { Story } from '@storybook/react';
import { ReactComponent as CalendarIcon } from 'assets/svg/calendar.svg';
import { ReactComponent as LogoIcon } from 'assets/svg/logo.svg';
import Input, { Props } from './Input';

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

export const Icon2 = Template.bind({});
Icon2.args = {
  icon: <CalendarIcon />,
  label: '이메일',
};

export const DefaultMessage = Template.bind({});
DefaultMessage.args = {
  status: 'default',
  message: '이메일을 입력해주세요.',
  label: '이메일',
};

export const SuccessMessage = Template.bind({});
SuccessMessage.args = {
  status: 'success',
  message: '사용할 수 있는 이메일입니다.',
  label: '이메일',
};

export const ErrorMessage = Template.bind({});
ErrorMessage.args = {
  status: 'error',
  message: '이미 존재하는 이메일입니다.',
  label: '이메일',
};
