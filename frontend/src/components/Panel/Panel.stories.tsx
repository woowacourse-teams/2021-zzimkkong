import { Story } from '@storybook/react';
import PALETTE from 'constants/palette';
import { PropsWithChildren } from 'react';
import Panel, { Props } from './Panel';

export default {
  title: 'shared/Panel',
  component: Panel,
  argTypes: {
    onToggle: { action: 'toggle' },
  },
};

const Template: Story<PropsWithChildren<Props>> = (args) => <Panel {...args} />;

export const Default = Template.bind({});
Default.args = {
  expandable: false,
  expanded: false,
  children: (
    <>
      <Panel.Header>
        <Panel.Title>패널 헤더</Panel.Title>
      </Panel.Header>
      <Panel.Content>
        <p>패널 내용</p>
      </Panel.Content>
    </>
  ),
};

export const BgColor = Template.bind({});
BgColor.args = {
  expandable: false,
  expanded: false,
  children: (
    <>
      <Panel.Header bgColor={PALETTE.ORANGE[500]}>
        <Panel.Title>패널 헤더</Panel.Title>
      </Panel.Header>
      <Panel.Content>
        <p>패널 내용</p>
      </Panel.Content>
    </>
  ),
};

export const Expandable = Template.bind({});
Expandable.args = {
  expandable: true,
  expanded: false,
  children: (
    <>
      <Panel.Header>
        <Panel.Title>패널 헤더</Panel.Title>
      </Panel.Header>
      <Panel.Content>
        <p>패널 내용</p>
      </Panel.Content>
    </>
  ),
};
