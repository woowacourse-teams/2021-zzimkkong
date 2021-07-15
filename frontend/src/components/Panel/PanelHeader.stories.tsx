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
  bgColor: '',
  children: <Panel.Title>패널 헤더 1</Panel.Title>,
};

export const BgColor = Template.bind({});
BgColor.args = {
  bgColor: PALETTE.ORANGE[500],
  children: <Panel.Title>패널 헤더 1</Panel.Title>,
};

export const BgColorWithInner = Template.bind({});
BgColorWithInner.args = {
  bgColor: PALETTE.ORANGE[500],
  children: (
    <Panel.Inner>
      <Panel.Title>패널 헤더 1</Panel.Title>
    </Panel.Inner>
  ),
};
