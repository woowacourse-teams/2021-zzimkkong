import { Story } from '@storybook/react';
import { PropsWithChildren } from 'react';
import Select, { Props } from './Select';

export default {
  title: 'shared/Select',
  component: Select,
};

const Template: Story<PropsWithChildren<Props>> = (args) => <Select {...args} />;

export const Default = Template.bind({});
Default.args = {
  value: '1',
  setValue: () => null,
  options: [
    {
      value: '1',
      children: <p>항목 1</p>,
    },
    {
      value: '2',
      children: <p>항목 2</p>,
    },
    {
      value: '3',
      children: <p>항목 3</p>,
    },
  ],
};
