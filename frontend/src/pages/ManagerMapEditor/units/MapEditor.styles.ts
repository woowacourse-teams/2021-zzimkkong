import styled, { css } from 'styled-components';
import IconButton from 'components/IconButton/IconButton';
import Input from 'components/Input/Input';

interface ToolbarButtonProps {
  selected?: boolean;
}

const primaryIconCSS = css`
  svg {
    fill: ${({ theme }) => theme.primary[400]};
  }
`;

export const Editor = styled.div`
  position: relative;
  display: flex;
  flex: 1;
  border-top: 1px solid ${({ theme }) => theme.gray[400]};
  border-bottom: 1px solid ${({ theme }) => theme.gray[400]};
  user-select: none;
`;

export const Toolbar = styled.div`
  padding: 1rem 0.5rem;
  background-color: ${({ theme }) => theme.gray[100]};
  border-left: 1px solid ${({ theme }) => theme.gray[400]};
  border-right: 1px solid ${({ theme }) => theme.gray[400]};
  display: flex;
  position: relative;
  flex-direction: column;
  gap: 1rem;
`;

export const ToolbarButton = styled(IconButton)<ToolbarButtonProps>`
  background-color: ${({ theme, selected }) => (selected ? theme.gray[100] : 'none')};
  border: 1px solid ${({ theme, selected }) => (selected ? theme.gray[400] : 'transparent')};
  border-radius: 0;
  box-sizing: content-box;

  ${({ selected }) => selected && primaryIconCSS}
`;

export const ColorPicker = styled.div`
  position: absolute;
  left: 3.75rem;
  top: 19rem;
`;

export const Board = styled.div`
  flex: 1;
`;

export const InputWrapper = styled.div`
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin: 0 0.25rem;
`;

export const Label = styled.div`
  color: ${({ theme }) => theme.gray[500]};
  text-align: center;
  user-select: none;
`;

export const LabelIcon = styled.div``;

export const LabelText = styled.div`
  font-size: 0.625rem;
`;

export const SizeInput = styled(Input)`
  border: 0;
  border-bottom: 2px solid ${({ theme }) => theme.gray[400]};
  border-radius: 0;
  padding: 0 0.25rem;
  margin-bottom: 0.25rem;
  width: 3rem;
  text-align: center;
  color: ${({ theme }) => theme.gray[500]};

  &:focus {
    border-top-width: 0;
    border-left-width: 0;
    border-right-width: 0;
    box-shadow: none;
    color: ${({ theme }) => theme.black[400]};
  }
`;

// Note: 맵 요소 단일 선택 시 보여질 circle 요소. 현재 미사용 중.
export const GripPoint = styled.circle`
  fill: ${({ theme }) => theme.white};
  stroke: ${({ theme }) => theme.black[100]};
  stroke-width: 2px;
  cursor: pointer;
`;
