import styled from 'styled-components';

const Loader = styled.div`
  width: 64px;
  height: 64px;
  position: relative;
  background-repeat: no-repeat;
  background-size: 16px 16px;
  background-image: ${({ theme }) => `
    linear-gradient(${theme.gray[200] ?? '#E4E4E7'} 16px, transparent 0),
    linear-gradient(${theme.primary[500] ?? '#FF7515'} 16px, transparent 0),
    linear-gradient(${theme.primary[500] ?? '#FF7515'} 16px, transparent 0),
    linear-gradient(${theme.gray[200] ?? '#E4E4E7'} 16px, transparent 0)`};
  background-position: left top, left bottom, right top, right bottom;
  animation: rotate 1s linear infinite;

  @keyframes rotate {
    0% {
      width: 64px;
      height: 64px;
      transform: rotate(0deg);
    }
    50% {
      width: 30px;
      height: 30px;
      transform: rotate(180deg);
    }
    100% {
      width: 64px;
      height: 64px;
      transform: rotate(360deg);
    }
  }
`;

export default Loader;
