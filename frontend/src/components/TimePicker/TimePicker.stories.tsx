import { Story } from '@storybook/react';
import TimePicker, { Props } from './TimePicker';

export default {
  title: 'shared/TimePicker',
  component: TimePicker,
  argTypes: {
    step: {
      options: [1, 5, 10, 15, 20, 30],
      control: { type: 'radio' },
    },
  },
};

const Template: Story<Props> = (args) => <TimePicker {...args} />;

export const Default = Template.bind({});
Default.args = {
  label: '기간 선택',
  step: 5,
};

export const WithoutLabel = Template.bind({});
WithoutLabel.args = {
  step: 1,
};
