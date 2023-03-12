import { AxiosError } from 'axios';
import { useEffect, useState } from 'react';
import { useMutation } from 'react-query';
import { Link, useHistory } from 'react-router-dom';
import { putMemberPassword } from 'api/member';
import Button from 'components/Button/Button';
import Header from 'components/Header/Header';
import Input from 'components/Input/Input';
import Layout from 'components/Layout/Layout';
import MANAGER from 'constants/manager';
import MESSAGE from 'constants/message';
import PATH from 'constants/path';
import REGEXP from 'constants/regexp';
import useInputs from 'hooks/useInputs';
import { ErrorResponse } from 'types/response';
import * as Styled from './ManagerPasswordEdit.styles';

interface Form {
  prevPassword: string;
  password: string;
  passwordConfirm: string;
}

const ManagerPasswordEdit = () => {
  const history = useHistory();

  const [{ prevPassword, password, passwordConfirm }, onChangeForm] = useInputs<Form>({
    prevPassword: '',
    password: '',
    passwordConfirm: '',
  });

  const [passwordMessage, setPasswordMessage] = useState('');
  const [passwordConfirmMessage, setPasswordConfirmMessage] = useState('');

  const editPassword = useMutation(putMemberPassword, {
    onSuccess: () => {
      history.push(PATH.MANAGER_MAP_LIST);
    },

    onError: (error: AxiosError<ErrorResponse>) => {
      alert(error?.response?.data.message ?? MESSAGE.MEMBER.EDIT_PROFILE_UNEXPECTED_ERROR);
    },
  });

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    editPassword.mutate({
      newPassword: password,
      newPasswordConfirm: passwordConfirm,
      oldPassword: prevPassword,
    });
  };

  const isValidPassword = REGEXP.PASSWORD.test(password);
  const isValidPasswordConfirm = password === passwordConfirm;

  const isSubmitButtonDisabled = !(
    prevPassword &&
    password &&
    passwordConfirm &&
    isValidPassword &&
    isValidPasswordConfirm
  );

  const handleCancel = () => {
    history.push(PATH.MANAGER_PROFILE_EDIT);
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
    <>
      <Header />
      <Layout>
        <Styled.ContainerForm onSubmit={handleSubmit}>
          <Styled.PageTitle>내 비밀번호 수정</Styled.PageTitle>
          <Styled.InputWrapper>
            <Input
              type="password"
              label="이전 비밀번호"
              name="prevPassword"
              minLength={MANAGER.PASSWORD.MIN_LENGTH}
              maxLength={MANAGER.PASSWORD.MAX_LENGTH}
              value={prevPassword}
              onChange={onChangeForm}
              required
            />
          </Styled.InputWrapper>
          <Styled.InputWrapper>
            <Input
              type="password"
              label="신규 비밀번호"
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
              label="신규 비밀번호 확인"
              name="passwordConfirm"
              minLength={MANAGER.PASSWORD.MIN_LENGTH}
              maxLength={MANAGER.PASSWORD.MAX_LENGTH}
              value={passwordConfirm}
              onChange={onChangeForm}
              message={passwordConfirmMessage}
              status={isValidPasswordConfirm ? 'success' : 'error'}
              required
            />
          </Styled.InputWrapper>
          <Styled.ButtonContainer>
            <Button size="large" type="button" fullWidth onClick={handleCancel}>
              취소
            </Button>
            <Button variant="primary" size="large" fullWidth disabled={isSubmitButtonDisabled}>
              수정
            </Button>
          </Styled.ButtonContainer>
        </Styled.ContainerForm>
      </Layout>
    </>
  );
};

export default ManagerPasswordEdit;
