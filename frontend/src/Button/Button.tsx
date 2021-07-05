import { ButtonHTMLAttributes } from 'react';
import styled from 'styled-components';

const ButtonStyled = styled.button`
  background: transparent;
  border-radius: 3px;
  border: 2px solid palevioletred;
  color: palevioletred;
  margin: 0 1em;
  padding: 0.25em 1em;
`;

export interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  text: string;
}

const Button = ({ text }: ButtonProps): JSX.Element => (
  <>
    <ButtonStyled>{text}</ButtonStyled>
    <h1>가나다</h1>
  </>
);

export default Button;
