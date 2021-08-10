import { Link } from 'react-router-dom';
import styled from 'styled-components';
import Layout from 'components/Layout/Layout';
import { Z_INDEX } from 'constants/style';

export const Header = styled.header`
  position: sticky;
  top: 0;
  height: 3rem;
  background-color: ${({ theme }) => theme.white};
  box-shadow: 0 0.25rem 0.25rem 0 ${({ theme }) => theme.shadow};
  z-index: ${Z_INDEX.HEADER};
`;

export const HeaderLayout = styled(Layout)`
  display: flex;
  align-items: center;
  height: 100%;
`;

export const HeaderLink = styled(Link)`
  display: flex;
  align-items: center;
  text-decoration: none;
  color: ${({ theme }) => theme.black[400]};
  cursor: pointer;
`;

export const Logo = styled.div`
  width: 1.75rem;
`;

export const Title = styled.h1`
  font-size: 1.5rem;
  font-weight: 700;
  margin-left: 0.5rem;
  vertical-align: middle;
  transform: translateY(-0.125rem);
`;
