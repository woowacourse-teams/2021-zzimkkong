import { Story } from '@storybook/react';
import { ReactComponent as MoreIcon } from 'assets/svg/more.svg';
import IconButton from 'components/IconButton/IconButton';
import MapListItem, { Props } from './MapListItem';

export default {
  title: 'shared/MapListItem',
  component: MapListItem,
};

const Template: Story<Props> = (args) => <MapListItem {...args} />;

const thumbnail = `<svg xmlns="http://www.w3.org/2000/svg" version="1.1" width="800px" height="600px" viewBox="0 0 800 600"> <rect x="260" y="180" width="130" height="210" stroke="#333333" fill="none" strokewidth="3"></rect> </svg>`;

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
