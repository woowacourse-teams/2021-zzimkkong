import { ButtonHTMLAttributes } from 'react';
import * as Styled from './IconButton.styles';

export interface Props extends ButtonHTMLAttributes<HTMLButtonElement> {
  size?: 'small' | 'medium' | 'large' | 'xLarge';
  text?: string;
}

const IconButton = ({ children, size = 'medium', text, ...props }: Props): JSX.Element => {
  return (
    <Styled.Button size={size} {...props}>
      <Styled.IconWrapper size={size}>{children}</Styled.IconWrapper>
      {text && <Styled.Text size={size}>{text}</Styled.Text>}
    </Styled.Button>
  );
};

export default IconButton;
