import { Story } from '@storybook/react';
import { ReactComponent as DeleteIcon } from 'assets/svg/delete.svg';
import IconButton, { Props } from './IconButton';

export default {
  title: 'shared/IconButton',
  component: IconButton,
};

const Template: Story<Props> = (args) => <IconButton {...args} />;

export const Default = Template.bind({});
Default.args = {
  children: <DeleteIcon />,
};

export const WithText = Template.bind({});
WithText.args = {
  children: <DeleteIcon />,
  text: '삭제',
};

export const Small = Template.bind({});
Small.args = {
  children: <DeleteIcon width="100%" height="100%" />,
  size: 'small',
};

export const SmallWithText = Template.bind({});
SmallWithText.args = {
  children: <DeleteIcon width="100%" height="100%" />,
  size: 'small',
  text: '삭제',
};

export const Large = Template.bind({});
Large.args = {
  children: <DeleteIcon width="100%" height="100%" />,
  size: 'large',
};

export const LargeWithText = Template.bind({});
LargeWithText.args = {
  children: <DeleteIcon width="100%" height="100%" />,
  size: 'large',
  text: '삭제',
};
