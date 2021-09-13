import { MouseEventHandler, PropsWithChildren } from 'react';
import ReactDOM from 'react-dom';
import { ReactComponent as CloseIcon } from 'assets/svg/close.svg';
import MESSAGE from 'constants/message';
import * as Styled from './Modal.styles';

export interface Props {
  open: boolean;
  isClosableDimmer?: boolean;
  showCloseButton?: boolean;
  onClose: () => void;
}

const modalRoot = document.getElementById('modal');

const Modal = ({
  open,
  isClosableDimmer,
  showCloseButton,
  onClose,
  children,
}: PropsWithChildren<Props>): JSX.Element => {
  if (modalRoot === null) throw new Error(MESSAGE.COMPONENTS.CANNOT_FIND_MODAL_ROOT);

  const handleMouseDownOverlay: MouseEventHandler<HTMLDivElement> = ({ target, currentTarget }) => {
    if (isClosableDimmer && target === currentTarget) {
      onClose();
    }
  };

  return ReactDOM.createPortal(
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

Modal.Inner = Styled.Inner;
Modal.Header = Styled.Header;
Modal.Content = Styled.Content;

export default Modal;
