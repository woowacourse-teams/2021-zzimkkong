import { Story } from '@storybook/react';
import { ReactComponent as MoreIcon } from 'assets/svg/more.svg';
import IconButton from 'components/IconButton/IconButton';
import { MapItemResponse } from 'types/response';
import MapListItem, { Props } from './MapListItem';

export default {
  title: 'shared/MapListItem',
  component: MapListItem,
};

const Template: Story<Props> = (args) => <MapListItem {...args} />;

const thumbnail = `<svg xmlns="http://www.w3.org/2000/svg" version="1.1" width="800px" height="600px" viewBox="0 0 800 600"> <rect x="260" y="180" width="130" height="210" stroke="#333333" fill="none" strokewidth="3"></rect> </svg>`;

const map: MapItemResponse = {
  mapId: 0,
  mapName: '루터회관 14F',
  mapDrawing: '',
  thumbnail,
  sharingMapId: 'test-0',
  notice: null,
};

export const Default = Template.bind({});
Default.args = {
  map,
};

export const WithControl = Template.bind({});
WithControl.args = {
  map,
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
  map,
  control: (
    <>
      <IconButton size="small">
        <MoreIcon width="100%" height="100%" />
      </IconButton>
    </>
  ),
};
