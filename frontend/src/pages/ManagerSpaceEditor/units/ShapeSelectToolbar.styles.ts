import styled, { css } from 'styled-components';
import IconButton from 'components/IconButton/IconButton';

interface ToolbarButtonProps {
  selected?: boolean;
}

const primaryIconCSS = css`
  svg {
    fill: ${({ theme }) => theme.primary[400]};
  }
`;

export const Container = styled.div`
  position: absolute;
  top: 0.5rem;
  left: 50%;
  transform: translateX(-50%);
  margin: 0 auto;
  padding: 0.5rem 1rem;
  background-color: ${({ theme }) => theme.gray[100]};
  border-right: 1px solid ${({ theme }) => theme.gray[300]};
  display: flex;
  gap: 1rem;
`;

export const ToolbarButton = styled(IconButton)<ToolbarButtonProps>`
  background-color: ${({ theme, selected }) => (selected ? theme.gray[100] : 'none')};
  border: 1px solid ${({ theme, selected }) => (selected ? theme.gray[400] : 'transparent')};
  border-radius: 0;
  box-sizing: content-box;
  ${({ selected }) => selected && primaryIconCSS}
`;
