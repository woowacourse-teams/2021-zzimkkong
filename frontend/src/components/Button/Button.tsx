import { ButtonHTMLAttributes, PropsWithChildren } from 'react';
import * as Styled from './Button.styles';

export interface Props extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant: 'primary' | 'default';
  size: 'small' | 'medium' | 'large';
  fullWidth: boolean;
}

const Button = ({
  variant = 'default',
  size = 'medium',
  fullWidth = false,
  children,
  ...props
}: PropsWithChildren<Props>): JSX.Element => (
  <Styled.Button variant={variant} size={size} fullWidth={fullWidth} {...props}>
    {children}
  </Styled.Button>
);

export default Button;
