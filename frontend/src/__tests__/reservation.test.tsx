import userEvent from '@testing-library/user-event';
import { MemoryRouter, Route, Switch } from 'react-router-dom';
import { HREF } from 'constants/path';
import { PUBLIC_ROUTES } from 'constants/routes';
import { render, screen, waitFor } from 'test-utils';
import { formatDate } from 'utils/datetime';

describe('사용자 예약', () => {
  beforeAll(() => {
    const sharingMapId = 'JMTGR';

    render(
      <MemoryRouter initialEntries={[HREF.GUEST_MAP(sharingMapId)]}>
        <Switch>
          {PUBLIC_ROUTES.map(({ path, component }) => (
            <Route exact path={path} key={path}>
              {component}
            </Route>
          ))}
        </Switch>
      </MemoryRouter>
    );
  });

  it('예약 목록을 조회할 수 있다. (date: 2021-07-01 / spaceId: 1)', async () => {
    const date = '2021-07-01';
    const spaceId = 1;

    const $targetSpace = await waitFor(() => screen.getByTestId(spaceId));
    const $targetDateInput = screen.getByDisplayValue(formatDate(new Date()));

    userEvent.type($targetDateInput, date);
    userEvent.click($targetSpace);

    const reservations = await waitFor(() => screen.getAllByRole('listitem'));

    expect(reservations).toHaveLength(2);
  });
});
