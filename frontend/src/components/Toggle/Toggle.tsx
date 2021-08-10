import { InputHTMLAttributes } from 'react';
import * as Styled from './Toggle.styles';

export interface Props extends InputHTMLAttributes<HTMLInputElement> {
  variant?: 'default' | 'primary';
  text: string;
  checkedText: string;
  textPosition?: 'left' | 'right';
}

const Toggle = ({
  variant = 'default',
  text,
  checkedText,
  checked,
  textPosition = 'right',
  ...props
}: Props): JSX.Element => {
  return (
    <Styled.Label>
      {textPosition === 'left' && (
        <Styled.LabelText>{checked ? checkedText : text}</Styled.LabelText>
      )}
      <Styled.Toggle>
        <Styled.Input type="checkbox" variant={variant} checked={checked} {...props} />
        <Styled.Slider />
      </Styled.Toggle>
      {textPosition === 'right' && (
        <Styled.LabelText>{checked ? checkedText : text}</Styled.LabelText>
      )}
    </Styled.Label>
  );
};

export default Toggle;
