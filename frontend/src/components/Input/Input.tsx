import { InputHTMLAttributes, ReactNode } from 'react';
import * as Styled from './Input.styles';

export interface Props extends InputHTMLAttributes<HTMLInputElement> {
  icon?: ReactNode;
  label?: string;
  status?: 'success' | 'error' | 'default';
  message?: string;
}

const Input = ({ icon, label, status = 'default', message, ...props }: Props): JSX.Element => (
  <Styled.Label>
    {label && <Styled.LabelText>{label}</Styled.LabelText>}
    {icon && <Styled.Icon>{icon}</Styled.Icon>}
    <Styled.Input icon={icon} {...props} />
    <Styled.Message status={status}>{message}</Styled.Message>
  </Styled.Label>
);

export default Input;
