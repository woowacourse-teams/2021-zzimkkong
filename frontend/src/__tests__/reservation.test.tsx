import userEvent from '@testing-library/user-event';
import { MemoryRouter, Route, Switch } from 'react-router-dom';
import MESSAGE from 'constants/message';
import { HREF } from 'constants/path';
import { PUBLIC_ROUTES } from 'constants/routes';
import { render, screen, waitFor, within } from 'test-utils';
import { formatDate } from 'utils/datetime';

describe('예약 페이지 조회', () => {
  beforeEach(() => {
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

describe('예약 추가', () => {
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

  it('예약페이지에 진입할 수 있다.', async () => {
    const date = new Date();
    date.setDate(date.getDate() + 1);
    const targetDate = formatDate(date);
    const spaceId = 1;

    const $targetSpace = await waitFor(() => screen.getByTestId(spaceId));
    const $targetDateInput = screen.getByDisplayValue(formatDate(new Date()));

    userEvent.type($targetDateInput, targetDate);
    userEvent.click($targetSpace);

    const $reservationButton = await waitFor(() => screen.getByRole('link', { name: /예약하기/i }));
    userEvent.click($reservationButton);

    const $pageTitle = await waitFor(() => screen.getByTestId(/spaceName/i));

    expect($pageTitle).toHaveTextContent('testSpace');

    const $nameInput = await waitFor(() => screen.getByRole('textbox', { name: /이름/i }));
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

    // TODO 예약 완료 랜딩 페이지 작성 후 추가 테스트 작성
  });
});

describe('예약 삭제', () => {
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

  it('특정 예약을 삭제할 수 있다.', async () => {
    const date = '2021-07-01';
    const spaceId = 1;
    const reservationId = 2;

    const $targetSpace = await waitFor(() => screen.getByTestId(spaceId));
    const $targetDateInput = screen.getByDisplayValue(formatDate(new Date()));

    userEvent.type($targetDateInput, date);
    userEvent.click($targetSpace);

    const $targetReservation = await waitFor(() =>
      screen.getByTestId(`reservation-${reservationId}`)
    );

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
