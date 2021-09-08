import { AxiosError } from 'axios';
import React, { FormEventHandler, useEffect, useState } from 'react';
import { useQuery } from 'react-query';
import { queryValidateEmail } from 'api/join';
import Button from 'components/Button/Button';
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
  organization: string;
}

interface Props {
  onSubmit: ({ email, password, organization }: JoinParams) => void;
}

const JoinForm = ({ onSubmit }: Props): JSX.Element => {
  const [{ email, password, passwordConfirm, organization }, onChangeForm] = useInputs<Form>({
    email: '',
    password: '',
    passwordConfirm: '',
    organization: '',
  });

  const [emailMessage, setEmailMessage] = useState('');
  const [passwordMessage, setPasswordMessage] = useState('');
  const [passwordConfirmMessage, setPasswordConfirmMessage] = useState('');
  const [organizationMessage, setOrganizationMessage] = useState('');

  const isValidPassword = REGEXP.PASSWORD.test(password);
  const isValidOrganization = REGEXP.ORGANIZATION.test(organization);

  const isValidEmail = useQuery(['isValidEmail', email], queryValidateEmail, {
    enabled: false,
    retry: false,

    onSuccess: () => {
      setEmailMessage(MESSAGE.JOIN.VALID_EMAIL);
    },

    onError: (error: AxiosError<ErrorResponse>) => {
      setEmailMessage(error.response?.data.message ?? '');
    },
  });

  const handleValidateEmail = () => {
    if (!email) return;

    isValidEmail.refetch();
  };

  const handleSubmit: FormEventHandler<HTMLFormElement> = (event) => {
    event.preventDefault();

    if (password !== passwordConfirm) {
      alert(MESSAGE.JOIN.INVALID_PASSWORD_CONFIRM);

      return;
    }

    onSubmit({ email, password, organization });
  };

  useEffect(() => {
    if (!password) return;

    !isValidPassword
      ? setPasswordMessage(MESSAGE.JOIN.INVALID_PASSWORD)
      : setPasswordMessage(MESSAGE.JOIN.VALID_PASSWORD);
  }, [password, isValidPassword]);

  useEffect(() => {
    if (!password || !passwordConfirm) return;

    password !== passwordConfirm
      ? setPasswordConfirmMessage(MESSAGE.JOIN.INVALID_PASSWORD_CONFIRM)
      : setPasswordConfirmMessage(MESSAGE.JOIN.VALID_PASSWORD_CONFIRM);
  }, [password, passwordConfirm]);

  useEffect(() => {
    if (!organization) {
      setOrganizationMessage('');
      return;
    }

    !isValidOrganization
      ? setOrganizationMessage(MESSAGE.JOIN.INVALID_ORGANIZATION)
      : setOrganizationMessage(MESSAGE.JOIN.VALID_ORGANIZATION);
  }, [organization, isValidOrganization]);

  return (
    <Styled.Form onSubmit={handleSubmit}>
      <Input
        type="email"
        label="이메일"
        value={email}
        name="email"
        onChange={onChangeForm}
        onBlur={handleValidateEmail}
        message={emailMessage}
        status={isValidEmail.isSuccess ? 'success' : 'error'}
        required
        autoFocus
      />
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
      <Input
        type="text"
        label="조직명"
        name="organization"
        minLength={MANAGER.ORGANIZATION.MIN_LENGTH}
        value={organization}
        onChange={onChangeForm}
        message={organizationMessage}
        status={isValidOrganization ? 'success' : 'error'}
        required
      />
      <Button
        variant="primary"
        size="large"
        fullWidth
        disabled={!(email && password && passwordConfirm && organization)}
      >
        회원가입
      </Button>
    </Styled.Form>
  );
};

export default JoinForm;
