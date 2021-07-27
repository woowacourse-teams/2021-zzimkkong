import { useContext } from 'react';
import * as Styled from './DrawerCloseButton.styles';
import DrawerContext from './DrawerContext';

const DrawerCloseButton = (): JSX.Element => {
  const { onClose } = useContext(DrawerContext);

  return <Styled.CloseButton onClick={onClose} aria-label="닫기" />;
};

export default DrawerCloseButton;
