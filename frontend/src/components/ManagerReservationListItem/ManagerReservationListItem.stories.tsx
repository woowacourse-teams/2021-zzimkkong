import { Story } from '@storybook/react';
import { ReactComponent as DeleteIcon } from 'assets/svg/delete.svg';
import { ReactComponent as EditIcon } from 'assets/svg/edit.svg';
import IconButton from 'components/IconButton/IconButton';
import { ReservationStatus } from 'types/common';
import ManagerReservationListItem, { Props } from './ManagerReservationListItem';

export default {
  title: 'shared/ReservationListItem',
  component: ManagerReservationListItem,
};

const Template: Story<Props> = (args) => <ManagerReservationListItem {...args} />;

export const Default = Template.bind({});
Default.args = {
  reservation: {
    id: 1,
    name: '체프',
    description: '맛있는 커피를 내리는 법',
    startDateTime: '2021-07-09T20:00:00',
    endDateTime: '2021-07-09T21:00:00',
    isLoginReservation: false,
    isMyReservation: false,
  },
};

export const Control = Template.bind({});
Control.args = {
  reservation: {
    id: 2,
    name: '체프',
    description: '맛있는 커피를 내리는 법',
    startDateTime: '2021-07-09T20:00:00',
    endDateTime: '2021-07-09T21:00:00',
    isLoginReservation: false,
    isMyReservation: false,
  },
  control: (
    <>
      <IconButton size="small">
        <EditIcon />
      </IconButton>
      <IconButton size="small">
        <DeleteIcon />
      </IconButton>
    </>
  ),
};

export const StatusUsing = Template.bind({});
StatusUsing.args = {
  reservation: {
    id: 1,
    name: '체프',
    description: '맛있는 커피를 내리는 법',
    startDateTime: '2021-07-09T20:00:00',
    endDateTime: '2021-07-09T21:00:00',
    isLoginReservation: false,
    isMyReservation: false,
  },
  status: ReservationStatus.using,
};

export const StatusDone = Template.bind({});
StatusDone.args = {
  reservation: {
    id: 1,
    name: '체프',
    description: '맛있는 커피를 내리는 법',
    startDateTime: '2021-07-09T20:00:00',
    endDateTime: '2021-07-09T21:00:00',
    isLoginReservation: false,
    isMyReservation: false,
  },
  status: ReservationStatus.done,
};
