import { MouseEventHandler, PropsWithChildren } from 'react';
import { createPortal } from 'react-dom';
import { ReactComponent as CloseIcon } from 'assets/svg/close.svg';
import * as Styled from './SummaryModal.styles';

export interface Props {
  open: boolean;
  isClosableDimmer?: boolean;
  showCloseButton?: boolean;
  onClose: () => void;
}

let modalRoot = document.getElementById('modal');

const SummaryModal = ({
  open,
  isClosableDimmer,
  showCloseButton,
  onClose,
  children,
}: PropsWithChildren<Props>): JSX.Element => {
  if (modalRoot === null) {
    modalRoot = document.createElement('div');
    modalRoot.setAttribute('id', 'modal');
    document.body.appendChild(modalRoot);
  }

  const handleMouseDownOverlay: MouseEventHandler<HTMLDivElement> = ({ target, currentTarget }) => {
    if (isClosableDimmer && target === currentTarget) {
      onClose();
    }
  };

  return createPortal(
    <Styled.Overlay open={open} onMouseDown={handleMouseDownOverlay}>
      <Styled.Modal>
        {open && showCloseButton && (
          <Styled.CloseButton onClick={onClose}>
            <CloseIcon />
          </Styled.CloseButton>
        )}
        {open && children}
      </Styled.Modal>
    </Styled.Overlay>,
    modalRoot
  );
};

SummaryModal.Inner = Styled.Inner;
SummaryModal.Header = Styled.Header;
SummaryModal.Content = Styled.Content;

export default SummaryModal;
