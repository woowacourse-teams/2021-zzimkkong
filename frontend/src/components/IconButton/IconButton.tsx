import { ButtonHTMLAttributes } from 'react';
import * as Styled from './IconButton.styles';

export interface Props extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'gray' | 'default';
  size?: 'small' | 'medium' | 'large';
  text?: string;
}

const IconButton = ({
  children,
  variant = 'default',
  size = 'medium',
  text,
  ...props
}: Props): JSX.Element => {
  return (
    <Styled.Container>
      <Styled.Button size={size} variant={variant} {...props}>
        {children}
      </Styled.Button>
      {text && <Styled.Text size={size}>{text}</Styled.Text>}
    </Styled.Container>
  );
};

export default IconButton;
