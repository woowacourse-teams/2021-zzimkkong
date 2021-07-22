import { Story } from '@storybook/react';
import { PropsWithChildren } from 'react';
import styled from 'styled-components';
import Drawer, { Props } from './Drawer';

export default {
  title: 'shared/Drawer',
  component: Drawer,
  argTypes: {
    placement: {
      options: ['left', 'right', 'top', 'bottom'],
      control: { type: 'radio' },
    },
  },
};

const Skeleton = styled.div`
  height: 5rem;
  background: #cccccc;
  margin: 1rem 0;
`;

const Template: Story<PropsWithChildren<Props>> = (args) => <Drawer {...args} />;

export const Bottom = Template.bind({});
Bottom.args = {
  open: true,
  placement: 'bottom',
  onClose: () => null,
  children: <Drawer.Inner>내용을 입력해주세요</Drawer.Inner>,
};

export const BottomMiddleHeight = Template.bind({});
BottomMiddleHeight.args = {
  open: true,
  placement: 'bottom',
  onClose: () => null,
  children: (
    <Drawer.Inner>
      <Skeleton />
      <Skeleton />
      <Skeleton />
      <Skeleton />
    </Drawer.Inner>
  ),
};

export const BottomMaxHeight = Template.bind({});
BottomMaxHeight.args = {
  open: true,
  placement: 'bottom',
  onClose: () => null,
  children: (
    <Drawer.Inner>
      <Skeleton />
      <Skeleton />
      <Skeleton />
      <Skeleton />
      <Skeleton />
      <Skeleton />
      <Skeleton />
      <Skeleton />
      <Skeleton />
      <Skeleton />
      <Skeleton />
      <Skeleton />
    </Drawer.Inner>
  ),
};

export const BottomWithCloseButton = Template.bind({});
BottomWithCloseButton.args = {
  open: true,
  placement: 'bottom',
  onClose: () => null,
  children: (
    <Drawer.Inner>
      <Drawer.Header>
        <Drawer.HeaderText>헤더 제목이 아주아주 길면 어떻게 될까?</Drawer.HeaderText>
        <Drawer.CloseButton />
      </Drawer.Header>
      <Skeleton />
      <Skeleton />
      <Skeleton />
      <Skeleton />
    </Drawer.Inner>
  ),
};

export const Left = Template.bind({});
Left.args = {
  open: true,
  placement: 'left',
  onClose: () => null,
  children: <Drawer.Inner>내용을 입력해주세요</Drawer.Inner>,
};

export const LeftWithMaxwidth = Template.bind({});
LeftWithMaxwidth.args = {
  open: true,
  placement: 'left',
  maxwidth: '15rem',
  onClose: () => null,
  children: <Drawer.Inner>내용을 입력해주세요</Drawer.Inner>,
};
