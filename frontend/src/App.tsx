import { ThemeProvider } from 'styled-components';
import { theme, GlobalStyle } from './App.styles';

const App = (): JSX.Element => (
  <ThemeProvider theme={theme}>
    <GlobalStyle />
  </ThemeProvider>
);

export default App;
