import styled from 'styled-components';
import { Reset } from 'styled-reset';

const Title = styled.h1`
  color: red;
`;

const App = (): JSX.Element => (
  <>
    <Reset />
    <Title>찜꽁</Title>
  </>
);

export default App;
