import { Story } from '@storybook/react';
import Header from './Header';

export default {
  title: 'shared/Header',
  component: Header,
};

const Template: Story = (args) => <Header {...args} />;

export const Default = Template.bind({});
