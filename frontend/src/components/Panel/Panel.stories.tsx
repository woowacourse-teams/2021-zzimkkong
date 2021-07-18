import { Story } from '@storybook/react';
import { PropsWithChildren } from 'react';
import PALETTE from 'constants/palette';
import Panel, { Props } from './Panel';

export default {
  title: 'shared/Panel',
  component: Panel,
};

const Template: Story<PropsWithChildren<Props>> = (args) => <Panel {...args} />;

export const Default = Template.bind({});
Default.args = {
  expandable: false,
  initialExpanded: false,
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
  initialExpanded: false,
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
  initialExpanded: false,
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

export const WithInner = Template.bind({});
WithInner.args = {
  expandable: false,
  initialExpanded: false,
  children: (
    <>
      <Panel.Header>
        <Panel.Inner>
          <Panel.Title>패널 헤더</Panel.Title>
        </Panel.Inner>
      </Panel.Header>
      <Panel.Content>
        <Panel.Inner>
          <p>패널 내용</p>
        </Panel.Inner>
      </Panel.Content>
    </>
  ),
};
