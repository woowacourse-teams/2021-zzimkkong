import styled, { css } from 'styled-components';
import { Color } from 'types/common';

interface HeaderWrapperProps {
  expandable: boolean;
}

interface HeaderContentProps {
  dotColor?: Color;
}

interface ToggleProps {
  expanded: boolean;
}

export const HeaderWrapper = styled.div<HeaderWrapperProps>`
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: ${({ expandable }) => (expandable ? 'pointer' : 'default')};
  gap: 1rem;
  border-bottom: 1px solid ${({ theme }) => theme.black[400]};
  padding: 1rem 0.75rem;

  path {
    fill: ${({ theme }) => theme.gray[400]};
  }
`;

const dot = css<HeaderContentProps>`
  &::before {
    content: '';
    display: block;
    width: 1rem;
    height: 1rem;
    background-color: ${({ dotColor }) => dotColor};
    border-radius: 50%;
    margin-right: 0.75rem;
  }
`;

export const HeaderContent = styled.div<HeaderContentProps>`
  flex: 1;
  display: flex;
  align-items: center;

  ${({ dotColor }) => (dotColor ? dot : '')}
`;

export const Toggle = styled.div<ToggleProps>`
  margin-right: 0.75rem;
  background: none;
  border: none;
  line-height: 0;
  transform: ${({ expanded }) => (expanded ? 'rotate(180deg)' : 'rotate(0deg)')};
`;
