import { useQuery } from 'react-query';
import { AxiosError } from 'axios';
import * as Styled from './Join.styles';
import { getValidateEmail } from 'api/join';
import Button from 'components/Button/Button';
import Header from 'components/Header/Header';
import Input from 'components/Input/Input';
import Layout from 'components/Layout/Layout';
import MESSAGE from 'constants/message';
import useInput from 'hooks/useInput';

const Join = (): JSX.Element => {
  const [email, onChangeEmail] = useInput('');

  const isValidEmail = useQuery(['isValidEmail', email], getValidateEmail, {
    enabled: false,
    retry: false,
  });

  const getEmailMessage = () => {
    const { isFetched, isError, isSuccess } = isValidEmail;
    const error = isValidEmail.error as AxiosError;
    const message = error?.response?.data?.message as string;

    if (!isFetched) return '';

    if (isSuccess) {
      return MESSAGE.JOIN.VALID_EMAIL;
    }

    if (isError && message) {
      return message;
    }

    return MESSAGE.JOIN.UNEXPECTED_ERROR;
  };

  const handleValidateEmail = () => {
    if (!email) return;

    isValidEmail.refetch();
  };

  return (
    <>
      <Header />
      <Layout>
        <Styled.PageTitle>회원가입</Styled.PageTitle>
        <Styled.Container>
          <Styled.Form>
            <Input
              type="email"
              label="이메일"
              autoFocus
              value={email}
              onChange={onChangeEmail}
              onBlur={handleValidateEmail}
              message={getEmailMessage()}
              status={isValidEmail.isError ? 'error' : 'success'}
              required
            />
            <Input type="password" label="비밀번호" required />
            <Input type="password" label="비밀번호 확인" required />
            <Input type="text" label="조직명" required />
            <Button variant="primary" size="large" fullWidth>
              회원가입
            </Button>
          </Styled.Form>
        </Styled.Container>
      </Layout>
    </>
  );
};

export default Join;
