import { ChangeEventHandler, Dispatch, SetStateAction, useState } from 'react';
import { ReactComponent as PaletteIcon } from 'assets/svg/palette.svg';
import PALETTE from 'constants/palette';
import * as Styled from './ColorPicker.styles';

export interface Props {
  color: string;
  setColor: Dispatch<SetStateAction<string>>;
}

const colors = [
  PALETTE.BLACK[400],
  PALETTE.GRAY[400],
  PALETTE.ORANGE[400],
  PALETTE.RED[400],
  PALETTE.YELLOW[500],
  PALETTE.GREEN[400],
  PALETTE.BLUE[400],
  PALETTE.PURPLE[400],
];

const ColorPicker = ({ color, setColor }: Props): JSX.Element => {
  const [open, setOpen] = useState(false);

  const handleChangeColor: ChangeEventHandler<HTMLInputElement> = (event) => {
    setColor(event.target.value);
  };

  return (
    <Styled.Container>
      <Styled.PickerIcon color={color} onClick={() => setOpen(!open)} />
      <Styled.PickerWrapper open={open}>
        {colors.map((color) => (
          <Styled.PickerIcon key={color} onClick={() => setColor(color)} color={color} />
        ))}
        <Styled.InputWrapper>
          <Styled.Input type="text" maxLength={7} value={color} onChange={handleChangeColor} />
          <Styled.ColorInputLabel>
            <PaletteIcon />
            <Styled.ColorInput type="color" value={color} onChange={handleChangeColor} />
          </Styled.ColorInputLabel>
        </Styled.InputWrapper>
      </Styled.PickerWrapper>
    </Styled.Container>
  );
};

export default ColorPicker;
