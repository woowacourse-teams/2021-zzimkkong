import { Story } from '@storybook/react';
import { ReactComponent as MoreIcon } from 'assets/svg/more.svg';
import IconButton from 'components/IconButton/IconButton';
import MapListItem, { Props } from './MapListItem';

export default {
  title: 'shared/MapListItem',
  component: MapListItem,
};

const Template: Story<Props> = (args) => <MapListItem {...args} />;

const thumbnail = {
  src: './images/luther.png',
  alt: '루터회관 14F 공간',
};

export const Default = Template.bind({});
Default.args = {
  thumbnail,
  title: '루터회관 14F',
};

export const WithControl = Template.bind({});
WithControl.args = {
  thumbnail,
  title: '루터회관 14F',
  control: (
    <>
      <IconButton size="small">
        <MoreIcon width="100%" height="100%" />
      </IconButton>
    </>
  ),
};

export const LongTitleWithControl = Template.bind({});
LongTitleWithControl.args = {
  thumbnail,
  title: '우아한현제들 우아한테크코스 교육장 루터회관 14F',
  control: (
    <>
      <IconButton size="small">
        <MoreIcon width="100%" height="100%" />
      </IconButton>
    </>
  ),
};
