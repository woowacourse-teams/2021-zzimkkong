import { Story } from '@storybook/react';
import DateInput, { Props } from './DateInput';

export default {
  title: 'shared/DateInput',
  component: DateInput,
};

const Template: Story<Props> = (args) => <DateInput {...args} />;

export const Default = Template.bind({});
Default.args = {
  date: new Date(),
};
