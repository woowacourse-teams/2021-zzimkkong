import { Story } from '@storybook/react';
import { PropsWithChildren } from 'react';
import PALETTE from 'constants/palette';
import Panel from './Panel';
import { Props } from './PanelHeader';

export default {
  title: 'shared/PanelHeader',
  component: Panel.Header,
  argTypes: {
    bgColor: {
      control: { type: 'color' },
    },
  },
};

const Template: Story<PropsWithChildren<Props>> = (args) => <Panel.Header {...args} />;

export const Default = Template.bind({});
Default.args = {
  dotColor: '',
  children: <Panel.Title>패널 헤더 1</Panel.Title>,
};

export const DotColor = Template.bind({});
DotColor.args = {
  dotColor: PALETTE.ORANGE[500],
  children: <Panel.Title>패널 헤더 1</Panel.Title>,
};
