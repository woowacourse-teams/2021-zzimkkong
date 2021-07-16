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
  text: '백엔드 강의실',
  name: 'luther',
  coordinate: { x: 50, y: 50 },
};

export const Checked = Template.bind({});
Checked.args = {
  text: '백엔드 강의실',
  name: 'luther',
  coordinate: { x: 50, y: 50 },
  checked: true,
};

export const Disabled = Template.bind({});
Disabled.args = {
  text: '회의실 1',
  name: 'luther',
  coordinate: { x: 50, y: 50 },
  disabled: true,
};
