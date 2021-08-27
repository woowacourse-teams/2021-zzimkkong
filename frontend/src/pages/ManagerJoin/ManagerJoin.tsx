import { AxiosError } from 'axios';
import { FormEventHandler, useEffect, useState } from 'react';
import { useMutation, useQuery } from 'react-query';
import { Link, useHistory } from 'react-router-dom';
import { postJoin, queryValidateEmail } from 'api/join';
import Button from 'components/Button/Button';
import Header from 'components/Header/Header';
import Input from 'components/Input/Input';
import Layout from 'components/Layout/Layout';
import MANAGER from 'constants/manager';
import MESSAGE from 'constants/message';
import PATH from 'constants/path';
import REGEXP from 'constants/regexp';
import useInput from 'hooks/useInput';
import { ErrorResponse } from 'types/response';
import * as Styled from './ManagerJoin.styles';

const ManagerJoin = (): JSX.Element => {
  const [email, onChangeEmail] = useInput('');
  const [password, onChangePassword] = useInput('');
  const [passwordConfirm, onChangePasswordConfirm] = useInput('');
  const [organization, onChangeOrganization] = useInput('');

  const [emailMessage, setEmailMessage] = useState('');
  const [passwordMessage, setPasswordMessage] = useState('');
  const [passwordConfirmMessage, setPasswordConfirmMessage] = useState('');
  const [organizationMessage, setOrganizationMessage] = useState('');

  const history = useHistory();

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

  const join = useMutation(postJoin, {
    onSuccess: () => {
      alert(MESSAGE.JOIN.SUCCESS);
      history.push(PATH.MANAGER_LOGIN);
    },

    onError: (error: AxiosError<ErrorResponse>) => {
      alert(error?.response?.data.message ?? MESSAGE.JOIN.FAILURE);
    },
  });

  const handleValidateEmail = () => {
    if (!email) return;

    isValidEmail.refetch();
  };

  const handleSubmitJoinForm: FormEventHandler<HTMLFormElement> = (event) => {
    event.preventDefault();

    if (!email || !password || !passwordConfirm || !organization) return;

    if (password !== passwordConfirm) {
      alert(MESSAGE.JOIN.INVALID_PASSWORD_CONFIRM);

      return;
    }

    join.mutate({ email, password, organization });
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
    <>
      <Header />
      <Layout>
        <Styled.Container>
          <Styled.PageTitle>회원가입</Styled.PageTitle>
          <Styled.Form onSubmit={handleSubmitJoinForm}>
            <Input
              type="email"
              label="이메일"
              value={email}
              onChange={onChangeEmail}
              onBlur={handleValidateEmail}
              message={emailMessage}
              status={isValidEmail.isSuccess ? 'success' : 'error'}
              required
              autoFocus
            />
            <Input
              type="password"
              label="비밀번호"
              minLength={MANAGER.PASSWORD.MIN_LENGTH}
              maxLength={MANAGER.PASSWORD.MAX_LENGTH}
              value={password}
              onChange={onChangePassword}
              message={passwordMessage}
              status={isValidPassword ? 'success' : 'error'}
              required
            />
            <Input
              type="password"
              label="비밀번호 확인"
              minLength={MANAGER.PASSWORD.MIN_LENGTH}
              maxLength={MANAGER.PASSWORD.MAX_LENGTH}
              value={passwordConfirm}
              onChange={onChangePasswordConfirm}
              message={passwordConfirmMessage}
              status={password === passwordConfirm ? 'success' : 'error'}
              required
            />
            <Input
              type="text"
              label="조직명"
              minLength={MANAGER.ORGANIZATION.MIN_LENGTH}
              value={organization}
              onChange={onChangeOrganization}
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
            <Styled.JoinLinkMessage>
              이미 회원이신가요?
              <Link to={PATH.MANAGER_LOGIN}>로그인하기</Link>
            </Styled.JoinLinkMessage>
          </Styled.Form>
        </Styled.Container>
      </Layout>
    </>
  );
};

export default ManagerJoin;
