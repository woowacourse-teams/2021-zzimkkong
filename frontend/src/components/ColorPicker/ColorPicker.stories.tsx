import { Story } from '@storybook/react';
import ColorPicker, { Props } from './ColorPicker';

export default {
  title: 'shared/ColorPicker',
  component: ColorPicker,
};

const Template: Story<Props> = (args) => <ColorPicker {...args} />;

export const Default = Template.bind({});
Default.args = {
  open: true,
};
