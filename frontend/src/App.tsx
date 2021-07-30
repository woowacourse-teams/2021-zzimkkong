import { QueryClient, QueryClientProvider } from 'react-query';
import { ReactQueryDevtools } from 'react-query/devtools';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import { RecoilRoot } from 'recoil';
import { ThemeProvider } from 'styled-components';
import PrivateRoute from 'PrivateRoute';
import { PRIVATE_ROUTES, PUBLIC_ROUTES } from 'constants/routes';
import { GlobalStyle, theme } from './App.styles';
import NotFound from './pages/NotFound/NotFound';

const queryClient = new QueryClient();

const App = (): JSX.Element => {
  return (
    <QueryClientProvider client={queryClient}>
      <RecoilRoot>
        <ThemeProvider theme={theme}>
          <GlobalStyle />
          <Router>
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

              <Route component={NotFound} />
            </Switch>
          </Router>
        </ThemeProvider>
      </RecoilRoot>
      <ReactQueryDevtools initialIsOpen={false} />
    </QueryClientProvider>
  );
};

export default App;
