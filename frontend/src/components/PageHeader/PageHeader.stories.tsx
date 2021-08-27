import { Story } from '@storybook/react';
import { PropsWithChildren } from 'react';
import { ReactComponent as MenuIcon } from 'assets/svg/menu.svg';
import { ReactComponent as MoreIcon } from 'assets/svg/more.svg';
import IconButton from 'components/IconButton/IconButton';
import PageHeader, { Props } from './PageHeader';

export default {
  title: 'shared/PageHeader',
  component: PageHeader,
};

const Template: Story<PropsWithChildren<Props>> = (args) => <PageHeader {...args} />;

export const Default = Template.bind({});
Default.args = {
  leftButtons: (
    <IconButton>
      <MenuIcon />
    </IconButton>
  ),
  title: '예약 페이지',
  rightButtons: (
    <IconButton>
      <MoreIcon />
    </IconButton>
  ),
};

export const TitleOnly = Template.bind({});
TitleOnly.args = {
  title: '예약 페이지',
};

export const LeftButton = Template.bind({});
LeftButton.args = {
  title: '예약 페이지',
  leftButtons: (
    <IconButton>
      <MenuIcon />
    </IconButton>
  ),
};

export const RightButton = Template.bind({});
RightButton.args = {
  title: '예약 페이지',
  rightButtons: (
    <IconButton>
      <MoreIcon />
    </IconButton>
  ),
};
