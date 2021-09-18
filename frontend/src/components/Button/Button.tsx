import { ButtonHTMLAttributes, PropsWithChildren } from 'react';
import * as Styled from './Button.styles';

export interface Props extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'primary-text' | 'text' | 'default';
  shape?: 'default' | 'round';
  size?: 'dense' | 'small' | 'medium' | 'large';
  fullWidth?: boolean;
}

const Button = ({
  variant = 'default',
  shape = 'default',
  size = 'medium',
  fullWidth = false,
  children,
  ...props
}: PropsWithChildren<Props>): JSX.Element => (
  <Styled.Button variant={variant} shape={shape} size={size} fullWidth={fullWidth} {...props}>
    {children}
  </Styled.Button>
);

export default Button;
