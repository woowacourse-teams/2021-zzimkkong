import { Story } from '@storybook/react';
import Button, { Props } from './Button';

export default {
  title: 'shared/Button',
  component: Button,
};

const Template: Story<Props> = (args) => <Button {...args} />;

export const Default = Template.bind({});
Default.args = {
  variant: 'default',
  children: 'Button Test',
};

export const Primary = Template.bind({});
Primary.args = {
  variant: 'primary',
  children: 'Button Test',
};

export const Small = Template.bind({});
Small.args = {
  size: 'small',
  children: 'Button Test',
};

export const Medium = Template.bind({});
Medium.args = {
  size: 'medium',
  children: 'Button Test',
};

export const Large = Template.bind({});
Large.args = {
  size: 'large',
  children: 'Button Test',
};

export const FullWidth = Template.bind({});
FullWidth.args = {
  fullWidth: true,
  children: 'Button Test',
};
