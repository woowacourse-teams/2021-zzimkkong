import styled from 'styled-components';
import closeURL from '../../assets/svg/close.svg';

export const CloseButton = styled.button`
  border: none;
  background: ${`url(${closeURL}) no-repeat center`};
  width: 1.5rem;
  min-width: 1.5rem;
  height: 1.5rem;
  min-height: 1.5rem;
  line-height: 1.5rem;
  padding: 0;
  cursor: pointer;
`;
