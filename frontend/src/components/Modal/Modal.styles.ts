import styled from 'styled-components';

interface Props {
  open?: boolean;
}

export const Overlay = styled.div<Props>`
  display: ${({ open }) => (open ? 'flex' : 'none')};
  width: 100vw;
  height: 100vh;
  justify-content: center;
  align-items: center;
  background-color: ${({ theme }) => theme.modalOverlay};
`;

export const Modal = styled.div`
  width: 80%;
  min-width: 320px;
  max-width: 768px;
  position: relative;
  background-color: ${({ theme }) => theme.white};
`;

export const CloseButton = styled.div`
  position: absolute;
  top: 0.5rem;
  right: 1rem;
  width: 1rem;
  cursor: pointer;

  &:hover {
    opacity: 0.7;
  }
`;

export const Inner = styled.div`
  padding: 0.5rem 1rem;
`;

export const Header = styled.h1`
  font-weight: 700;
  padding: 1rem;
`;

export const Content = styled.div``;
