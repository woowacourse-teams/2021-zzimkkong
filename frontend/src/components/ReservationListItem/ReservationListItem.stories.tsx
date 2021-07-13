import { Story } from '@storybook/react';
import ReservationListItem, { Props } from './ReservationListItem';
import { ReactComponent as EditIcon } from 'assets/svg/edit.svg';
import { ReactComponent as DeleteIcon } from 'assets/svg/delete.svg';

export default {
  title: 'shared/ReservationListItem',
  component: ReservationListItem,
};

const Template: Story<Props> = (args) => <ReservationListItem {...args} />;

export const Default = Template.bind({});
Default.args = {
  reservation: {
    name: '체프',
    description: '맛있는 커피를 내리는 법',
    startDateTime: new Date('2021-07-09T20:00:00'),
    endDateTime: new Date('2021-07-09T21:00:00'),
  },
};

export const Control = Template.bind({});
Control.args = {
  reservation: {
    name: '체프',
    description: '맛있는 커피를 내리는 법',
    startDateTime: new Date('2021-07-09T20:00:00'),
    endDateTime: new Date('2021-07-09T21:00:00'),
  },
  control: (
    <>
      <EditIcon />
      <DeleteIcon />
    </>
  ),
};
