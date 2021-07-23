import styled from 'styled-components';

interface Props {
  open?: boolean;
}

export const Overlay = styled.div<Props>`
  display: ${({ open }) => (open ? 'flex' : 'none')};
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 101;
  justify-content: center;
  align-items: center;
  background-color: ${({ theme }) => theme.modalOverlay};
`;

export const Modal = styled.div`
  width: 80%;
  min-width: 320px;
  max-width: 768px;
  position: relative;
  overflow: hidden;
  border-radius: 4px;
  background-color: ${({ theme }) => theme.white};
`;

export const CloseButton = styled.button`
  position: absolute;
  padding: 0.5rem;
  top: 0.5rem;
  right: 1.5rem;
  width: 1rem;
  cursor: pointer;
  border: none;
  background: none;

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
  margin-bottom: 0.5rem;
`;

export const Content = styled.div``;
