import * as Styled from './Join.styles';
import Header from 'components/Header/Header';
import Layout from 'components/Layout/Layout';
import Input from 'components/Input/Input';
import Button from 'components/Button/Button';

const Join = (): JSX.Element => {
  return (
    <>
      <Header />
      <Layout>
        <Styled.PageTitle>회원가입</Styled.PageTitle>
        <Styled.Container>
          <Styled.Form>
            <Input type="email" label="이메일" autoFocus />
            <Input type="password" label="비밀번호" />
            <Input type="password" label="비밀번호 확인" />
            <Input type="text" label="조직명" />
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
