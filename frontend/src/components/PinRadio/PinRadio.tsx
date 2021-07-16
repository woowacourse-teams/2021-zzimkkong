import { InputHTMLAttributes } from 'react';
import { Coordinate } from 'types/common';
import { Color } from 'types/styled';
import * as Styled from './PinRadio.styles';

export interface Props extends InputHTMLAttributes<HTMLInputElement> {
  text: string;
  textPosition?: 'left' | 'right' | 'top' | 'bottom';
  coordinate: Coordinate;
  color?: Color;
}

const PinRadio = ({
  text,
  textPosition = 'bottom',
  color,
  coordinate,
  ...props
}: Props): JSX.Element => {
  return (
    <Styled.Pin coordinate={coordinate}>
      <Styled.PinContainer>
        <Styled.PinInput type="radio" color={color} {...props} />
        <Styled.PinText textPosition={textPosition}>{text}</Styled.PinText>
      </Styled.PinContainer>
    </Styled.Pin>
  );
};

export default PinRadio;
