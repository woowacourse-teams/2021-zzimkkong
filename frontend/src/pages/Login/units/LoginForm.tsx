import { FormEventHandler } from 'react';
import Button from 'components/Button/Button';
import Input from 'components/Input/Input';
import MANAGER from 'constants/manager';
import useInputs from 'hooks/useInputs';
import { ErrorMessage, LoginParams } from '../Login';

import * as Styled from './LoginForm.styles';

interface Form {
  email: string;
  password: string;
}

interface Props {
  errorMessage: ErrorMessage;
  onSubmit: ({ email, password }: LoginParams) => void;
}

const LoginForm = ({ errorMessage, onSubmit }: Props): JSX.Element => {
  const [{ email, password }, onChangeForm] = useInputs<Form>({
    email: '',
    password: '',
  });

  const handleSubmit: FormEventHandler<HTMLFormElement> = (event) => {
    event.preventDefault();

    onSubmit({ email, password });
  };

  return (
    <Styled.Form onSubmit={handleSubmit}>
      <Input
        type="email"
        label="이메일"
        name="email"
        value={email}
        onChange={onChangeForm}
        message={errorMessage?.email}
        status={errorMessage?.email ? 'error' : 'default'}
        autoFocus
        required
      />
      <Input
        type="password"
        label="비밀번호"
        name="password"
        value={password}
        minLength={MANAGER.PASSWORD.MIN_LENGTH}
        maxLength={MANAGER.PASSWORD.MAX_LENGTH}
        onChange={onChangeForm}
        message={errorMessage?.password}
        status={errorMessage?.password ? 'error' : 'default'}
        required
      />
      <Button variant="primary" size="large" fullWidth>
        로그인
      </Button>
    </Styled.Form>
  );
};

export default LoginForm;
