import { PropsWithChildren } from 'react';
import { useTransition, config } from 'react-spring';
import * as Styled from './Drawer.styles';
import DrawerCloseButton from './DrawerCloseButton';
import DrawerContext from './DrawerContext';

export interface Props {
  placement?: 'left' | 'right' | 'top' | 'bottom';
  open: boolean;
  maxwidth?: string | number;
  onClose: () => void;
}

const transitionStyle = {
  left: {
    from: {
      transform: 'translate(-105%, 0%)',
    },
    leave: {
      transform: 'translate(-105%, 0%)',
    },
  },
  right: {
    from: {
      transform: 'translate(105%, 0%)',
    },
    leave: {
      transform: 'translate(105%, 0%)',
    },
  },
  top: {
    from: {
      transform: 'translate(0%, -105%)',
    },
    leave: {
      transform: 'translate(0%, -105%)',
    },
  },
  bottom: {
    from: {
      transform: 'translate(0%, 105%)',
    },
    leave: {
      transform: 'translate(0%, 105%)',
    },
  },
};

const Drawer = ({
  open = false,
  placement = 'bottom',
  maxwidth = '64rem',
  onClose,
  children,
}: PropsWithChildren<Props>): JSX.Element => {
  const dimmerTransition = useTransition(open, {
    from: {
      opacity: 0,
    },
    enter: {
      opacity: 1,
    },
    leave: {
      opacity: 0,
    },
    config: config.default,
  });

  const containerTransition = useTransition(open, {
    ...transitionStyle[placement],
    enter: {
      transform: 'translate(0%, 0%)',
    },
    config: {
      tension: 300,
      friction: 34,
    },
  });

  const dimmer = dimmerTransition((style, flag) => {
    return <>{flag && <Styled.Dimmer onClick={onClose} style={style} />}</>;
  });

  const container = containerTransition((style, flag) => {
    return (
      <>
        {flag && (
          <Styled.Container placement={placement} maxwidth={maxwidth} style={style}>
            {children}
          </Styled.Container>
        )}
      </>
    );
  });

  const contextValue = {
    open,
    onClose,
  };

  return (
    <DrawerContext.Provider value={contextValue}>
      {dimmer}
      {container}
    </DrawerContext.Provider>
  );
};

Drawer.CloseButton = DrawerCloseButton;

Drawer.Inner = Styled.Inner;

Drawer.Header = Styled.Header;

Drawer.HeaderText = Styled.HeaderText;

export default Drawer;
