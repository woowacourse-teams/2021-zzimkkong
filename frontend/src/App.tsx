import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import { ThemeProvider } from 'styled-components';
import { theme, GlobalStyle, BaseLayout } from './App.styles';
import Login from './pages/Login/Login';
import Join from './pages/Join/Join';
import PATH from './constants/path';

const App = (): JSX.Element => (
  <ThemeProvider theme={theme}>
    <GlobalStyle />
    <BaseLayout>
      <Router>
        <Switch>
          <Route path={PATH.LOGIN}>
            <Login />
          </Route>
          <Route path={PATH.JOIN}>
            <Join />
          </Route>
        </Switch>
      </Router>
    </BaseLayout>
  </ThemeProvider>
);

export default App;
