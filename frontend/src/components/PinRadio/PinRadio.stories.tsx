import { Story } from '@storybook/react';
import styled from 'styled-components';
import PinRadio, { Props } from './PinRadio';

export default {
  title: 'shared/Pin',
  component: PinRadio,
};

const Background = styled.div`
  position: relative;
  width: 100%;
  height: 500px;
  background-color: #eee;
`;

const Template: Story<Props> = (args) => (
  <Background>
    <PinRadio {...args} />
  </Background>
);

export const Default = Template.bind({});
Default.args = {
  text: '회의실',
  name: 'luther',
  coordinate: { x: 50, y: 50 },
};

export const TextRight = Template.bind({});
TextRight.args = {
  text: '회의실',
  textPosition: 'right',
  name: 'luther',
  coordinate: { x: 50, y: 50 },
};

export const TextLeft = Template.bind({});
TextLeft.args = {
  text: '회의실',
  textPosition: 'left',
  name: 'luther',
  coordinate: { x: 70, y: 50 },
};

export const TextTop = Template.bind({});
TextTop.args = {
  text: '회의실',
  textPosition: 'top',
  name: 'luther',
  coordinate: { x: 50, y: 50 },
};

export const Checked = Template.bind({});
Checked.args = {
  text: '회의실',
  name: 'luther',
  coordinate: { x: 50, y: 50 },
  checked: true,
};

export const Disabled = Template.bind({});
Disabled.args = {
  text: '회의실',
  name: 'luther',
  coordinate: { x: 50, y: 50 },
  disabled: true,
};
