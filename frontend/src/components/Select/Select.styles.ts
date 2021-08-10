import styled from 'styled-components';
import { Z_INDEX } from 'constants/style';

interface CaretDownIconWrapperProps {
  open: boolean;
}

interface ListBoxProps {
  maxheight?: string | number;
}

export const Select = styled.div`
  position: relative;
`;

export const Label = styled.span`
  display: none;
`;

export const ListBoxButton = styled.button`
  position: relative;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 0.5rem;
  border: 1px solid ${({ theme }) => theme.gray[500]};
  border-radius: 0.25rem;
  background: ${({ theme }) => theme.white};
  padding: 0.75rem;
  width: 100%;
  font-size: 1rem;
  text-align: left;
  z-index: ${Z_INDEX.SELECT_LIST_BOX_BUTTON};
  cursor: pointer;

  &:focus {
    border-color: ${({ theme }) => theme.primary[400]};
    box-shadow: inset 0px 0px 0px 1px ${({ theme }) => theme.primary[400]};
  }

  &:disabled {
    background-color: ${({ theme }) => theme.gray[200]};
    cursor: default;
  }
`;

export const OptionChildrenWrapper = styled.div`
  flex: 1;
`;

export const CaretDownIconWrapper = styled.span<CaretDownIconWrapperProps>`
  display: inline-flex;
  justify-content: space-between;
  align-items: center;
  transform: ${({ open }) => (open ? 'matrix(1, 0, 0, -1, 0, 0)' : 'matrix(1, 0, 0, 1, 0, 0)')};
  transition: transform 0.2s ease;
`;

export const ListBox = styled.ul<ListBoxProps>`
  position: absolute;
  top: 100%;
  left: 0;
  width: 100%;
  max-height: ${({ maxheight }) => {
    if (!maxheight) return '15rem';
    if (typeof maxheight === 'number') return `${maxheight}px`;
    return maxheight;
  }};
  border: 1px solid ${({ theme }) => theme.gray[500]};
  border-bottom-left-radius: 0.25rem;
  border-bottom-right-radius: 0.25rem;
  transform: translateY(-0.125rem);
  overflow-y: auto;
  z-index: ${Z_INDEX.SELECT_LIST_BOX};

  &::before {
    position: relative;
    display: block;
    content: '';
    height: 0.125rem;
  }
`;

export const Option = styled.li`
  border-bottom: 1px solid ${({ theme }) => theme.gray[400]};
  background: ${({ theme }) => theme.white};
  padding: 0.75rem;
  line-height: normal;
  cursor: pointer;

  &:last-of-type {
    border-bottom: 0;
  }

  &:hover,
  &:focus {
    background: ${({ theme }) => theme.gray[100]};
  }
`;
