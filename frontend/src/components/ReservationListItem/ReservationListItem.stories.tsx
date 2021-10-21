import { Story } from '@storybook/react';
import { ReactComponent as DeleteIcon } from 'assets/svg/delete.svg';
import { ReactComponent as EditIcon } from 'assets/svg/edit.svg';
import IconButton from 'components/IconButton/IconButton';
import ReservationListItem, { Props } from './ReservationListItem';

export default {
  title: 'shared/ReservationListItem',
  component: ReservationListItem,
};

const Template: Story<Props> = (args) => <ReservationListItem {...args} />;

export const Default = Template.bind({});
Default.args = {
  reservation: {
    id: 1,
    name: '체프',
    description: '맛있는 커피를 내리는 법',
    startDateTime: '2021-07-09T20:00:00',
    endDateTime: '2021-07-09T21:00:00',
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
  },
  status: 'using',
};

export const StatusDone = Template.bind({});
StatusDone.args = {
  reservation: {
    id: 1,
    name: '체프',
    description: '맛있는 커피를 내리는 법',
    startDateTime: '2021-07-09T20:00:00',
    endDateTime: '2021-07-09T21:00:00',
  },
  status: 'done',
};
