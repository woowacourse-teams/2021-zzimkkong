import styled from 'styled-components';
import Layout from 'components/Layout/Layout';

export const Header = styled.header`
  position: sticky;
  top: 0;
  height: 4rem;
  background-color: ${({ theme }) => theme.white};
  z-index: 99;

  svg {
    padding: 0.875rem 0;
    height: 100%;
  }
`;

export const HeaderLayout = styled(Layout)`
  display: flex;
  align-items: center;
  height: inherit;
  width: 100%;
`;

export const Title = styled.h1`
  font-size: 1.625rem;
  font-weight: 700;
  margin-left: 0.375rem;
  transform: translateY(-0.125rem);
`;
