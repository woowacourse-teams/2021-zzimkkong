import { createBrowserHistory } from 'history';
import { Suspense } from 'react';
import { QueryClient, QueryClientProvider } from 'react-query';
import { ReactQueryDevtools } from 'react-query/devtools';
import { Route, Router, Switch } from 'react-router-dom';
import { ThemeProvider } from 'styled-components';
import PrivateRoute from 'PrivateRoute';
import Header from 'components/Header/Header';
import PATH from 'constants/path';
import { PRIVATE_ROUTES, PUBLIC_ROUTES } from 'constants/routes';
import AccessTokenProvider from 'providers/AccessTokenProvider';
import { GlobalStyle, theme } from './App.styles';
import NotFound from './pages/NotFound/NotFound';

export const history = createBrowserHistory();
export const queryClient = new QueryClient();

const App = (): JSX.Element => {
  return (
    <QueryClientProvider client={queryClient}>
      <AccessTokenProvider>
        <ThemeProvider theme={theme}>
          <GlobalStyle />
          <Router history={history}>
            <Suspense fallback={<Header />}>
              <Switch>
                {PUBLIC_ROUTES.map(({ path, component }) => (
                  <Route exact path={path} key={path}>
                    {component}
                  </Route>
                ))}

                {PRIVATE_ROUTES.map(({ path, component, redirectPath }) => (
                  <PrivateRoute exact path={path} redirectPath={redirectPath} key={path}>
                    {component}
                  </PrivateRoute>
                ))}
                <Route path={['*', PATH.NOT_FOUND]} component={NotFound} />
              </Switch>
            </Suspense>
          </Router>
        </ThemeProvider>
      </AccessTokenProvider>
      <ReactQueryDevtools initialIsOpen={false} />
    </QueryClientProvider>
  );
};

export default App;
