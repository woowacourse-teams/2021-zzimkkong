import styled from 'styled-components';

export const Page = styled.div`
  display: flex;
`;

export const HeaderWrapper = styled.div`
  position: fixed;
  width: calc(100% - 576px);
`;

export const MapContainer = styled.div`
  height: 100vh;
  flex: 1;
  overflow: auto;
  display: flex;
  flex-direction: column;
  position: relative;

  @media (max-width: ${({ theme: { breakpoints } }) => breakpoints.sm}px) {
    display: none;
  }
`;

export const SelectBox = styled.div`
  display: flex;
  flex-direction: column;
`;

export const SelectButton = styled.button`
  display: flex;
  align-items: center;
  border: none;
  cursor: pointer;
  background-color: white;
  padding: 1rem 1.5rem;
  text-align: left;
  font-size: 1rem;

  svg {
    margin-top: 0.25rem;
    margin-right: 0.5rem;
    fill: ${({ theme }) => theme.gray[400]};
  }

  &:hover {
    opacity: 0.7;
  }

  &:first-child {
    border-bottom: 1px solid rgba(196, 196, 196, 0.3);
  }
`;
