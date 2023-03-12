import { AxiosError } from 'axios';
import React, { FormEventHandler, useEffect, useState } from 'react';
import { useQuery } from 'react-query';
import { queryValidateEmail, queryValidateUserName } from 'api/join';
import Button from 'components/Button/Button';
import EmojiSelector from 'components/EmojiSelector/EmojiSelector';
import Input from 'components/Input/Input';
import MANAGER from 'constants/manager';
import MESSAGE from 'constants/message';
import REGEXP from 'constants/regexp';
import useInputs from 'hooks/useInputs';
import { ErrorResponse } from 'types/response';
import { JoinParams } from '../ManagerJoin';
import * as Styled from './JoinForm.styles';

interface Form {
  email: string;
  password: string;
  passwordConfirm: string;
  userName: string;
}

interface Props {
  onSubmit: ({ emoji, email, password, userName }: JoinParams) => void;
}

const JoinForm = ({ onSubmit }: Props): JSX.Element => {
  const [emoji, setEmoji] = useState<string>('');
  const [{ email, password, passwordConfirm, userName }, onChangeForm] = useInputs<Form>({
    email: '',
    password: '',
    passwordConfirm: '',
    userName: '',
  });

  const [emailMessage, setEmailMessage] = useState('');
  const [passwordMessage, setPasswordMessage] = useState('');
  const [passwordConfirmMessage, setPasswordConfirmMessage] = useState('');
  const [userNameMessage, setUserNameMessage] = useState('');

  const isValidPassword = REGEXP.PASSWORD.test(password);

  const checkValidateEmail = useQuery(['checkValidateEmail', email], queryValidateEmail, {
    enabled: false,
    retry: false,

    onSuccess: () => {
      setEmailMessage(MESSAGE.JOIN.VALID_EMAIL);
    },

    onError: (error: AxiosError<ErrorResponse>) => {
      setEmailMessage(error.response?.data.message ?? MESSAGE.JOIN.CHECK_EMAIL_UNEXPECTED_ERROR);
    },
  });

  const checkValidateUserName = useQuery(
    ['checkValidateUserName', userName],
    queryValidateUserName,
    {
      enabled: false,
      retry: false,

      onSuccess: () => {
        setUserNameMessage(MESSAGE.JOIN.VALID_USERNAME);
      },

      onError: (error: AxiosError<ErrorResponse>) => {
        setUserNameMessage(
          error.response?.data.message ?? MESSAGE.JOIN.CHECK_USERNAME_UNEXPECTED_ERROR
        );
      },
    }
  );

  const handleChangeEmail = (event: React.ChangeEvent<HTMLInputElement>) => {
    onChangeForm(event);
    setEmailMessage('');
  };

  const handleChangeUserName = (event: React.ChangeEvent<HTMLInputElement>) => {
    onChangeForm(event);
    setUserNameMessage('');
  };

  const handleValidateEmail = () => {
    if (!email) return;

    checkValidateEmail.refetch();
  };

  const handleValidateUserName = () => {
    if (!userName) return;

    checkValidateUserName.refetch();
  };

  const handleSelectEmoji = (emoji: string) => {
    setEmoji(emoji);
  };

  const handleSubmit: FormEventHandler<HTMLFormElement> = (event) => {
    event.preventDefault();

    if (password !== passwordConfirm) {
      alert(MESSAGE.JOIN.INVALID_PASSWORD_CONFIRM);

      return;
    }

    onSubmit({ emoji, email, password, userName });
  };

  useEffect(() => {
    if (!password) return;

    setPasswordMessage(
      isValidPassword ? MESSAGE.JOIN.VALID_PASSWORD : MESSAGE.JOIN.INVALID_PASSWORD
    );
  }, [password, isValidPassword]);

  useEffect(() => {
    if (!password || !passwordConfirm) return;

    setPasswordConfirmMessage(
      password === passwordConfirm
        ? MESSAGE.JOIN.VALID_PASSWORD_CONFIRM
        : MESSAGE.JOIN.INVALID_PASSWORD_CONFIRM
    );
  }, [password, passwordConfirm]);

  return (
    <Styled.Form onSubmit={handleSubmit}>
      <EmojiSelector onSelect={handleSelectEmoji} />

      <Styled.InputWrapper>
        <Input
          type="email"
          label="이메일"
          value={email}
          name="email"
          onChange={handleChangeEmail}
          onBlur={handleValidateEmail}
          message={emailMessage}
          status={checkValidateEmail.isSuccess ? 'success' : 'error'}
          required
          autoFocus
        />
      </Styled.InputWrapper>

      <Styled.InputWrapper>
        <Input
          type="password"
          label="비밀번호"
          name="password"
          minLength={MANAGER.PASSWORD.MIN_LENGTH}
          maxLength={MANAGER.PASSWORD.MAX_LENGTH}
          value={password}
          onChange={onChangeForm}
          message={passwordMessage}
          status={isValidPassword ? 'success' : 'error'}
          required
        />
      </Styled.InputWrapper>

      <Styled.InputWrapper>
        <Input
          type="password"
          label="비밀번호 확인"
          name="passwordConfirm"
          minLength={MANAGER.PASSWORD.MIN_LENGTH}
          maxLength={MANAGER.PASSWORD.MAX_LENGTH}
          value={passwordConfirm}
          onChange={onChangeForm}
          message={passwordConfirmMessage}
          status={password === passwordConfirm ? 'success' : 'error'}
          required
        />
      </Styled.InputWrapper>

      <Styled.InputWrapper>
        <Input
          type="text"
          label="이름"
          name="userName"
          minLength={MANAGER.USERNAME.MIN_LENGTH}
          maxLength={MANAGER.USERNAME.MAX_LENGTH}
          value={userName}
          onChange={handleChangeUserName}
          onBlur={handleValidateUserName}
          message={userNameMessage}
          status={checkValidateUserName.isSuccess ? 'success' : 'error'}
          required
        />
      </Styled.InputWrapper>

      <Button
        variant="primary"
        size="large"
        fullWidth
        disabled={!(emoji && email && password && passwordConfirm)}
      >
        회원가입
      </Button>
    </Styled.Form>
  );
};

export default JoinForm;
