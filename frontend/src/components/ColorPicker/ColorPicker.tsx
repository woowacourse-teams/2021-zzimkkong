import { ChangeEventHandler, Dispatch, SetStateAction } from 'react';
import { ReactComponent as PaletteIcon } from 'assets/svg/palette.svg';
import { MAP_COLOR_PALETTE } from 'constants/editor';
import * as Styled from './ColorPicker.styles';
import ColorPickerIcon from './ColorPickerIcon';

export interface Props {
  open: boolean;
  color: string;
  setColor: Dispatch<SetStateAction<string>>;
}

const ColorPicker = ({ open, color, setColor }: Props): JSX.Element => {
  const handleChangeColor: ChangeEventHandler<HTMLInputElement> = (event) => {
    setColor(event.target.value);
  };

  return (
    <Styled.Container open={open}>
      {MAP_COLOR_PALETTE.map((color) => (
        <ColorPickerIcon key={color} onClick={() => setColor(color)} color={color} />
      ))}
      <Styled.InputWrapper>
        <Styled.Input type="text" maxLength={7} value={color} onChange={handleChangeColor} />
        <Styled.ColorInputLabel>
          <PaletteIcon />
          <Styled.ColorInput type="color" value={color} onChange={handleChangeColor} />
        </Styled.ColorInputLabel>
      </Styled.InputWrapper>
    </Styled.Container>
  );
};

export default ColorPicker;
