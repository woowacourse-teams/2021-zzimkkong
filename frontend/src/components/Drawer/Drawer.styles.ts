import { animated } from 'react-spring';
import styled, { css } from 'styled-components';

interface ContainerProps {
  placement: 'left' | 'right' | 'top' | 'bottom';
  maxwidth: string | number;
}

const placementCSS = {
  left: css`
    transform: translate(-105%, 0%);
    left: 0;
    top: 0;
    width: 80%;
    height: 100%;
    border-top-right-radius: 1rem;
    border-bottom-right-radius: 1rem;
    box-shadow: 0.25rem 0 0.25rem 0 ${({ theme }) => theme.shadow};
  `,
  right: css`
    transform: translate(105%, 0%);
    right: 0;
    top: 0;
    width: 80%;
    height: 100%;
    border-top-left-radius: 1rem;
    border-bottom-left-radius: 1rem;
    box-shadow: -0.25rem 0 0.25rem 0 ${({ theme }) => theme.shadow};
  `,
  top: css`
    transform: translate(0%, -105%);
    left: 0;
    right: 0;
    top: 0;
    width: 100%;
    max-height: 70%;
    border-bottom-left-radius: 1rem;
    border-bottom-right-radius: 1rem;
    box-shadow: 0 0.25rem 0.25rem 0 ${({ theme }) => theme.shadow};
  `,
  bottom: css`
    transform: translate(0%, 105%);
    left: 0;
    right: 0;
    bottom: 0;
    width: 100%;
    max-height: 70%;
    border-top-left-radius: 1rem;
    border-top-right-radius: 1rem;
    box-shadow: 0 -0.25rem 0.25rem 0 ${({ theme }) => theme.shadow};
  `,
};

export const Dimmer = styled(animated.div)`
  background: ${({ theme }) => theme.modalOverlay};
  display: flex;
  align-items: flex-end;
  justify-content: center;
  position: fixed;
  top: 0;
  right: 0;
  left: 0;
  bottom: 0;
  z-index: 100;
`;

export const Container = styled(animated.div)<ContainerProps>`
  margin: 0 auto;
  position: fixed;
  background: ${({ theme }) => theme.white};
  z-index: 101;
  overflow: auto;
  max-width: ${({ maxwidth }) => {
    if (!maxwidth) return '64rem';
    if (typeof maxwidth === 'number') return `${maxwidth}px`;
    return maxwidth;
  }};
  ${({ placement = 'bottom' }) => placementCSS[placement]};
`;

export const Inner = styled.div`
  padding: 2rem;
`;

export const Header = styled.div`
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 2rem;
`;

export const HeaderText = styled.p`
  font-size: 1.5rem;
`;
