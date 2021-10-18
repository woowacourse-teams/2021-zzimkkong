import { Story } from '@storybook/react';
import useTimePicker from '../../hooks/useTimePicker';
import TimePicker, { Step } from './TimePicker';

export default {
  title: 'shared/TimePicker',
  component: TimePicker,
  argTypes: {
    step: {
      options: [1, 5, 10, 15, 20, 30, 60],
      control: { type: 'radio' },
    },
  },
};

interface Props {
  label?: string;
  defaultStartTime?: Date;
  defaultEndTime?: Date;
  step?: Step;
}

const Template: Story<Props> = (args) => {
  const timePicker = useTimePicker(args);

  return <TimePicker {...timePicker} label={args?.label} step={args?.step} />;
};

export const Default = Template.bind({});
Default.args = {
  label: '기간 선택',
  step: 5,
};

export const HasDefaultTime = Template.bind({});
HasDefaultTime.args = {
  label: '기본 값이 존재하는 경우',
  step: 15,
  defaultStartTime: new Date(),
  defaultEndTime: new Date(),
};

export const WithoutLabel = Template.bind({});
WithoutLabel.args = {
  step: 10,
};
