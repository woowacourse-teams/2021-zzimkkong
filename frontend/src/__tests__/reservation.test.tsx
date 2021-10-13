import userEvent from '@testing-library/user-event';
import { MemoryRouter, Route, Switch } from 'react-router-dom';
import MESSAGE from 'constants/message';
import { HREF } from 'constants/path';
import { PUBLIC_ROUTES } from 'constants/routes';
import { render, screen, waitFor, within } from 'test-utils';
import { formatDate } from 'utils/datetime';

describe('예약 페이지', () => {
  const date = '2021-07-01';
  const nowDate = new Date();
  const spaceId = 1;
  const reservationId = 2;

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

  it('예약 목록을 조회할 수 있다.', async () => {
    const $targetSpace = await screen.findByTestId(spaceId);
    const $targetDateInput = screen.getByDisplayValue(formatDate(new Date()));

    userEvent.type($targetDateInput, date);
    userEvent.click($targetSpace);

    const reservations = await screen.findAllByRole('listitem');

    expect(reservations).toHaveLength(2);
  });

  it('예약을 추가할 수 있다.', async () => {
    const targetDate = formatDate(nowDate);

    const $targetSpace = await screen.findByTestId(spaceId);
    const $targetDateInput = screen.getByDisplayValue('2021-07-01');

    userEvent.type($targetDateInput, targetDate);
    userEvent.click($targetSpace);

    const $reservationButton = await screen.findByRole('button', { name: /예약하기/i });

    userEvent.click($reservationButton);

    const $pageTitle = await screen.findByTestId(/spaceName/i);

    expect($pageTitle).toHaveTextContent('testSpace');

    const $nameInput = await screen.findByRole('textbox', { name: /이름/i });
    const $descriptionInput = screen.getByRole('textbox', { name: /사용 목적/i });
    const $startTimeInput = screen.getByLabelText(/시작 시간/i);
    const $endTimeInput = screen.getByLabelText(/종료 시간/i);
    const $passwordInput = screen.getByLabelText(/비밀번호숫자 4자리를 입력해주세요\./i);
    const $submitButton = screen.getByRole('button', { name: /예약하기/i });

    userEvent.type($nameInput, '유조');
    userEvent.type($descriptionInput, '찜꽁 회의');
    userEvent.type($startTimeInput, '15:30');
    userEvent.type($endTimeInput, '16:30');
    userEvent.type($passwordInput, '1117');
    userEvent.click($submitButton);

    const $link = await screen.findByRole('link', { name: /맵으로 돌아가기/i });

    userEvent.click($link);

    const $heading = await screen.findByRole('heading', { name: /GUEST_TEST_MAP/i });
    expect($heading).toBeTruthy();
  });

  it('예약을 삭제할 수 있다.', async () => {
    const $targetSpace = await screen.findByTestId(spaceId);
    const $targetDateInput = screen.getByDisplayValue(formatDate(nowDate));

    userEvent.type($targetDateInput, date);
    userEvent.click($targetSpace);

    const $targetReservation = screen.getByTestId(`reservation-${reservationId}`);
    const $deleteButton = within($targetReservation).getByRole('button', { name: /삭제/i });

    userEvent.click($deleteButton);

    const $passwordInput = screen.getByLabelText(/비밀번호/i);
    const $submitButton = screen.getByRole('button', { name: /확인/i });

    userEvent.type($passwordInput, '1111');
    userEvent.click($submitButton);

    await waitFor(() =>
      expect(window.alert).toHaveBeenCalledWith(MESSAGE.RESERVATION.DELETE_SUCCESS)
    );
  });
});
