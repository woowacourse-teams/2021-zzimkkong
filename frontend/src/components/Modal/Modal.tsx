import { MouseEventHandler, PropsWithChildren, useState } from 'react';
import { ReactComponent as CloseIcon } from 'assets/svg/close.svg';
import * as Styled from './Modal.styles';

export interface Props {
  open?: boolean;
  isClosableDimmer?: boolean;
  showCloseButton?: boolean;
  setModalOpen?: (open: boolean) => void;
}

const Modal = ({
  open,
  isClosableDimmer,
  showCloseButton,
  setModalOpen,
  children,
}: PropsWithChildren<Props>): JSX.Element => {
  const handleDimmedClick: MouseEventHandler<HTMLDivElement> = ({ target, currentTarget }) => {
    if (isClosableDimmer && target === currentTarget) {
      setModalOpen?.(false);
    }
  };

  const handleClose = () => {
    setModalOpen?.(false);
  };

  return (
    <Styled.Overlay open={open} onClick={handleDimmedClick}>
      <Styled.Modal>
        {showCloseButton && (
          <Styled.CloseButton onClick={handleClose}>
            <CloseIcon />
          </Styled.CloseButton>
        )}
        {children}
      </Styled.Modal>
    </Styled.Overlay>
  );
};

Modal.Inner = Styled.Inner;
Modal.Header = Styled.Header;
Modal.Content = Styled.Content;

export default Modal;
