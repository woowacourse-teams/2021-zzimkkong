import { AxiosError, AxiosResponse } from 'axios';
import { FormEventHandler, useState } from 'react';
import { useMutation } from 'react-query';
import { useHistory } from 'react-router';
import { useSetRecoilState } from 'recoil';
import { postLogin } from 'api/login';
import Button from 'components/Button/Button';
import Input from 'components/Input/Input';
import MANAGER from 'constants/manager';
import MESSAGE from 'constants/message';
import PATH from 'constants/path';
import { LOCAL_STORAGE_KEY } from 'constants/storage';
import useInputs from 'hooks/useInputs';
import accessTokenState from 'state/accessTokenState';
import { ErrorResponse, LoginSuccess } from 'types/response';
import { setLocalStorageItem } from 'utils/localStorage';
import * as Styled from './LoginForm.styles';

interface ErrorMessage {
  email?: string;
  password?: string;
}

interface Form {
  email: string;
  password: string;
}

const LoginForm = (): JSX.Element => {
  const history = useHistory();
  const setAccessToken = useSetRecoilState(accessTokenState);

  const [{ email, password }, onChangeForm] = useInputs<Form>({
    email: '',
    password: '',
  });

  const [errorMessage, setErrorMessage] = useState<ErrorMessage>({
    email: '',
    password: '',
  });

  const login = useMutation(postLogin, {
    onSuccess: (response: AxiosResponse<LoginSuccess>) => {
      const { accessToken } = response.data;

      setLocalStorageItem({ key: LOCAL_STORAGE_KEY.ACCESS_TOKEN, item: accessToken });
      setAccessToken(accessToken);

      history.push(PATH.MANAGER_MAIN);
    },
    onError: (error: AxiosError<ErrorResponse>) => {
      const field = error.response?.data.field;
      const message = error.response?.data.message;

      if (field && message) {
        setErrorMessage({ [field]: message });
        return;
      }

      setErrorMessage({ password: message ?? MESSAGE.LOGIN.UNEXPECTED_ERROR });
    },
  });

  const handleSubmit: FormEventHandler<HTMLFormElement> = (event) => {
    event.preventDefault();

    if (!(email && password)) return;

    login.mutate({ email, password });
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
