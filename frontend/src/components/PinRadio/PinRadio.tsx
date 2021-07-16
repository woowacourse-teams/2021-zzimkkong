import { InputHTMLAttributes } from 'react';
import { Coordinate } from 'types/common';
import { Color } from 'types/styled';
import * as Styled from './PinRadio.styles';

export interface Props extends InputHTMLAttributes<HTMLInputElement> {
  text: string;
  coordinate: Coordinate;
  color?: Color;
}

const PinRadio = ({ text, color, coordinate, ...props }: Props): JSX.Element => {
  return (
    <Styled.Pin coordinate={coordinate}>
      <Styled.PinContainer>
        <Styled.PinInput type="radio" color={color} {...props} />
        <Styled.PinText>{text}</Styled.PinText>
      </Styled.PinContainer>
    </Styled.Pin>
  );
};

export default PinRadio;
