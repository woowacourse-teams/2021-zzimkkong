import { Story } from '@storybook/react';
import { PropsWithChildren } from 'react';
import Toggle, { Props } from './Toggle';

export default {
  title: 'shared/Toggle',
  component: Toggle,
  argTypes: {
    variant: {
      options: ['default', 'primary'],
      control: { type: 'radio' },
    },
    textPosition: {
      options: ['left', 'right'],
      control: { type: 'radio' },
    },
  },
};

const Template: Story<PropsWithChildren<Props>> = (args) => <Toggle {...args} />;

export const Default = Template.bind({});
Default.args = {
  variant: 'default',
  checked: false,
  uncheckedText: '토글 비활성화됨',
  checkedText: '토글 활성화됨',
  textPosition: 'left',
};

export const Primary = Template.bind({});
Primary.args = {
  variant: 'primary',
  checked: false,
  uncheckedText: '토글 비활성화됨',
  checkedText: '토글 활성화됨',
  textPosition: 'right',
};
