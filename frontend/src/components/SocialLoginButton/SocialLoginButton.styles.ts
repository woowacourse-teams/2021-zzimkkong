import styled, { css } from 'styled-components';
import PALETTE from 'constants/palette';
import { Props } from './SocialLoginButton';

const providerCSS = {
  github: css`
    background-color: ${PALETTE.GITHUB};
    color: ${PALETTE.WHITE};
    border: none;
  `,
  google: css`
    background-color: ${PALETTE.WHITE};
    color: ${PALETTE.BLACK[700]};
    border: 1px solid ${PALETTE.BLACK[700]};
  `,
};

export const SocialLoginButton = styled.a<Props>`
  ${({ provider }) => providerCSS[provider]};
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
  text-decoration: none;
  padding: 0.75rem 1rem;
  font-size: 1.25rem;
`;

export const Icon = styled.div`
  display: inline-flex;
  align-items: center;
  justify-content: flex-end;
`;

export const Text = styled.div``;
