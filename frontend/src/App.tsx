import { QueryClient, QueryClientProvider } from 'react-query';
import { ReactQueryDevtools } from 'react-query/devtools';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import { RecoilRoot } from 'recoil';
import { ThemeProvider } from 'styled-components';
import UserReservationEdit from 'pages/UserReservationEdlit/UserReservationEdit';
import { GlobalStyle, theme } from './App.styles';
import PATH from './constants/path';
import Join from './pages/Join/Join';
import Login from './pages/Login/Login';
import Main from './pages/Main/Main';
import ProviderReservationList from './pages/ProviderReservationList/ProviderReservationList';
import UserMain from './pages/UserMain/UserMain';
import UserReservation from './pages/UserReservation/UserReservation';

const queryClient = new QueryClient();

const App = (): JSX.Element => (
  <QueryClientProvider client={queryClient}>
    <RecoilRoot>
      <ThemeProvider theme={theme}>
        <GlobalStyle />
        <Router>
          <Switch>
            <Route exact path={PATH.HOME}>
              <Main />
            </Route>
            <Route exact path={PATH.LOGIN}>
              <Login />
            </Route>
            <Route exact path={PATH.JOIN}>
              <Join />
            </Route>
            <Route exact path={PATH.USER_MAIN}>
              <UserMain />
            </Route>
            <Route exact path={PATH.RESERVATION}>
              <UserReservation />
            </Route>
            <Route exact path={PATH.RESERVATION_EDIT}>
              <UserReservationEdit />
            </Route>
            <Route exact path={PATH.PROVIDER_RESERVATION_LIST}>
              <ProviderReservationList />
            </Route>
          </Switch>
        </Router>
      </ThemeProvider>
    </RecoilRoot>
    <ReactQueryDevtools initialIsOpen={false} />
  </QueryClientProvider>
);

export default App;
