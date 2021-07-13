import { QueryClient, QueryClientProvider } from 'react-query';
import { ReactQueryDevtools } from 'react-query/devtools';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import { RecoilRoot } from 'recoil';
import { ThemeProvider } from 'styled-components';
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
            <Route path={PATH.LOGIN}>
              <Login />
            </Route>
            <Route path={PATH.JOIN}>
              <Join />
            </Route>
            <Route path={PATH.RESERVATION}>
              <UserReservation />
            </Route>
          </Switch>
        </Router>
      </ThemeProvider>
    </RecoilRoot>
    <ReactQueryDevtools initialIsOpen={false} />
  </QueryClientProvider>
);

export default App;
