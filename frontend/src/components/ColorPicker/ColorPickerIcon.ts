import styled from 'styled-components';

interface ColorPickerIconProps {
  color: string;
}

const ColorPickerIcon = styled.div<ColorPickerIconProps>`
  width: 1.5rem;
  height: 1.5rem;
  border-radius: 50%;
  cursor: pointer;
  background-color: ${({ color }) => color};
`;

export default ColorPickerIcon;
