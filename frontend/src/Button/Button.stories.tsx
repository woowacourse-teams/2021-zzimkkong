import { Story } from '@storybook/react';
import Button, { ButtonProps } from './Button';

export default {
  title: 'shared/Button',
  component: Button,
};

const Template: Story<ButtonProps> = (args) => <Button {...args} />;

export const Default = Template.bind({});
Default.args = {
  text: 'Button Test',
};
