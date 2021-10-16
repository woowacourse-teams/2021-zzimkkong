import styled from 'styled-components';
import PALETTE from 'constants/palette';
import { Z_INDEX } from './../../constants/style';

export const OptionsContainer = styled.div`
  width: 100%;
  border: 1px solid ${({ theme }) => theme.gray[500]};
  border-top: 0;
  border-radius: 0 0 0.125rem 0.125rem;
  display: flex;
  align-items: center;
  height: 10rem;
  overflow: hidden;
  position: relative;
  z-index: ${Z_INDEX.TIME_PICKER};
  background-color: ${({ theme }) => theme.white};

  &::before {
    content: '';
    display: block;
    width: inherit;
    height: 2rem;
    background-color: ${PALETTE.OPACITY_BLACK[50]};
    position: absolute;
    top: calc(4rem + 0.5px);
    pointer-events: none;
  }
`;

export const OptionContainer = styled.div`
  flex: 1;
  height: inherit;
  overflow-y: auto;
  text-align: center;

  &::-webkit-scrollbar {
    display: none;
  }
`;

export const Option = styled.label`
  width: 100%;
  height: 2rem;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  color: ${({ theme }) => theme.gray[400]};

  &:first-child {
    margin-top: 4rem;
  }

  &:last-child {
    margin-bottom: 4rem;
  }
`;

export const OptionText = styled.span`
  display: flex;
  justify-content: center;
  align-items: center;
  width: inherit;
  height: inherit;
`;

export const Radio = styled.input`
  appearance: none;

  &:checked + ${OptionText} {
    color: ${({ theme }) => theme.gray[600]};
    font-weight: 600;
  }

  &:focus + ${OptionText} {
    box-shadow: inset 0 -0.125rem ${({ theme }) => theme.primary[400]};
  }
`;
