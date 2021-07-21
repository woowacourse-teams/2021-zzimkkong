import { QueryClient, QueryClientProvider } from 'react-query';
import { ReactQueryDevtools } from 'react-query/devtools';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import { RecoilRoot } from 'recoil';
import { ThemeProvider } from 'styled-components';
import Main from 'pages/Main/Main';
import ProviderReservationList from 'pages/ProviderReservationList/ProviderReservationList';
import UserMain from 'pages/UserMain/UserMain';
import { GlobalStyle, theme } from './App.styles';
import PATH from './constants/path';
import Join from './pages/Join/Join';
import Login from './pages/Login/Login';
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
            <Route path={PATH.LOGIN}>
              <Login />
            </Route>
            <Route path={PATH.JOIN}>
              <Join />
            </Route>
            <Route path={PATH.USER_MAIN}>
              <UserMain />
            </Route>
            <Route path={PATH.RESERVATION}>
              <UserReservation />
            </Route>
            <Route path={PATH.PROVIDER_RESERVATION_LIST}>
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
