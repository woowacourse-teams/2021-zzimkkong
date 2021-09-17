import styled, { css } from 'styled-components';
import PALETTE from 'constants/palette';
import { Props as JoinButtonProps } from './SocialJoinButton';
import { Props as LoginButtonProps } from './SocialLoginButton';

const providerCSS = {
  GITHUB: css`
    background-color: ${PALETTE.GITHUB};
    color: ${PALETTE.WHITE};
    border: none;
  `,
  GOOGLE: css`
    background-color: ${PALETTE.WHITE};
    color: ${PALETTE.BLACK[700]};
    border: 1px solid ${PALETTE.BLACK[700]};
  `,
};

const buttonCSS = css`
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
  text-decoration: none;
  padding: 0.75rem 1rem;
  font-size: 1.25rem;
  border-radius: 0.125rem;
`;

export const SocialLoginButton = styled.a<LoginButtonProps>`
  ${({ provider }) => providerCSS[provider]}
  ${buttonCSS};
`;

export const SocialJoinButton = styled.button<JoinButtonProps>`
  ${({ provider }) => providerCSS[provider]}
  ${buttonCSS};
  width: 100%;
  cursor: pointer;
`;

export const Icon = styled.div`
  display: inline-flex;
  align-items: center;
  justify-content: flex-end;
`;

export const Text = styled.div``;
