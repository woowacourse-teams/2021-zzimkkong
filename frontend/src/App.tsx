import styled, { ThemeProvider } from 'styled-components';
import { Reset } from 'styled-reset';
import theme from './theme';

const Title = styled.h1`
  color: red;
`;

const App = (): JSX.Element => (
  <ThemeProvider theme={theme}>
    <Reset />
    <Title>찜꽁</Title>
  </ThemeProvider>
);

export default App;
