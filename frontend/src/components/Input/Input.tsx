import { InputHTMLAttributes, ReactNode } from 'react';
import * as Styled from './Input.styles';

export interface Props extends InputHTMLAttributes<HTMLInputElement> {
  icon?: ReactNode;
  label?: string;
}

const Input = ({ icon, label, ...props }: Props): JSX.Element => (
  <Styled.Label>
    {label && <Styled.LabelText>{label}</Styled.LabelText>}
    {icon && <Styled.Icon>{icon}</Styled.Icon>}
    <Styled.Input icon={icon} {...props} />
  </Styled.Label>
);

export default Input;
