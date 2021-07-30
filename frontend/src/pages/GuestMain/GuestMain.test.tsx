import userEvent from '@testing-library/user-event';
import { render, screen, waitFor } from 'test-utils';
import { formatDate } from 'utils/datetime';
import GuestMain from './GuestMain';

describe('예약자 메인 페이지', () => {
  beforeAll(() => {
    render(<GuestMain />);
  });

  it('예약 목록을 조회할 수 있다. (date: 2021-07-01 / spaceId: 1)', async () => {
    const date = '2021-07-01';
    const spaceId = 1;

    const $targetSpaceRadio = screen.getByDisplayValue(`${spaceId}`);
    const $targetDateInput = screen.getByDisplayValue(formatDate(new Date()));

    userEvent.type($targetDateInput, date);
    userEvent.click($targetSpaceRadio);

    const reservations = await waitFor(() => screen.getAllByRole('listitem'));

    expect(reservations).toHaveLength(2);
  });
});
