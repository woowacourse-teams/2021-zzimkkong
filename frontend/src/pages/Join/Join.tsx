import { postJoin, queryValidateEmail } from 'api/join';
import { AxiosError } from 'axios';
import Button from 'components/Button/Button';
import Header from 'components/Header/Header';
import Input from 'components/Input/Input';
import Layout from 'components/Layout/Layout';
import MESSAGE from 'constants/message';
import PATH from 'constants/path';
import REGEXP from 'constants/regexp';
import useInput from 'hooks/useInput';
import { FormEventHandler, useEffect, useState } from 'react';
import { useMutation, useQuery } from 'react-query';
import { useHistory } from 'react-router-dom';
import * as Styled from './Join.styles';

const Join = (): JSX.Element => {
  const [email, onChangeEmail] = useInput('');
  const [password, onChangePassword] = useInput('');
  const [passwordConfirm, onChangePasswordConfirm] = useInput('');
  const [organization, onChangeOrganization] = useInput('');

  const [emailMessage, setEmailMessage] = useState('');
  const [passwordMessage, setPasswordMessage] = useState('');
  const [passwordConfirmMessage, setPasswordConfirmMessage] = useState('');

  const history = useHistory();

  const isValidEmail = useQuery(['isValidEmail', email], queryValidateEmail, {
    enabled: false,
    retry: false,

    onSuccess: () => {
      setEmailMessage(MESSAGE.JOIN.VALID_EMAIL);
    },

    onError: (error: AxiosError) => {
      setEmailMessage(error?.response?.data.message);
    },
  });

  const joinUser = useMutation(postJoin, {
    onSuccess: () => {
      alert(MESSAGE.JOIN.SUCCESS);
      history.push(PATH.LOGIN);
    },

    onError: () => {
      alert(MESSAGE.JOIN.FAILURE);
    },
  });

  const handleValidateEmail = () => {
    if (!email) return;

    isValidEmail.refetch();
  };

  const handleSubmitJoinForm: FormEventHandler<HTMLFormElement> = (event) => {
    event.preventDefault();

    if (!email || !password || !passwordConfirm || !organization) return;

    joinUser.mutate({ email, password, organization });
  };

  useEffect(() => {
    if (password !== passwordConfirm) {
      setPasswordConfirmMessage(MESSAGE.JOIN.INVALID_PASSWORD_CONFIRM);
    } else {
      setPasswordConfirmMessage(MESSAGE.JOIN.VALID_PASSWORD_CONFIRM);
    }
  }, [passwordConfirm]);

  useEffect(() => {
    if (!REGEXP.PASSWORD.test(password)) {
      setPasswordMessage(MESSAGE.JOIN.INVALID_PASSWORD);
    } else {
      setPasswordMessage(MESSAGE.JOIN.VALID_PASSWORD);
    }
  }, [password]);

  return (
    <>
      <Header />
      <Layout>
        <Styled.PageTitle>회원가입</Styled.PageTitle>
        <Styled.Container>
          <Styled.Form onSubmit={handleSubmitJoinForm}>
            <Input
              type="email"
              label="이메일"
              autoFocus
              value={email}
              onChange={onChangeEmail}
              onBlur={handleValidateEmail}
              message={emailMessage}
              status={isValidEmail.isSuccess ? 'success' : 'error'}
              required
            />
            <Input
              type="password"
              label="비밀번호"
              minLength={8}
              value={password}
              onChange={onChangePassword}
              message={passwordMessage}
              status={REGEXP.PASSWORD.test(password) ? 'success' : 'error'}
              required
            />
            <Input
              type="password"
              label="비밀번호 확인"
              minLength={8}
              value={passwordConfirm}
              onChange={onChangePasswordConfirm}
              message={passwordConfirmMessage}
              status={password === passwordConfirm ? 'success' : 'error'}
              required
            />
            <Input
              type="text"
              label="조직명"
              minLength={1}
              value={organization}
              onChange={onChangeOrganization}
              required
            />
            <Button
              variant="primary"
              size="large"
              fullWidth
              disabled={email && password && passwordConfirm && organization ? false : true}
            >
              회원가입
            </Button>
          </Styled.Form>
        </Styled.Container>
      </Layout>
    </>
  );
};

export default Join;
