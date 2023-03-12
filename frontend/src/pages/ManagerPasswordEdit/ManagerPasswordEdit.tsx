import Header from 'components/Header/Header';
import Layout from 'components/Layout/Layout';

const ManagerPasswordEdit = () => {
  return (
    <>
      <Header />
      <Layout>
        {/* <Styled.Container>
          <Styled.PageTitle>내 정보 수정</Styled.PageTitle>
          <ProfileEditForm onSubmit={handleSubmit} />
          <Styled.PasswordChangeLinkMessage>
            비밀번호를 변경하고 싶으신가요?
            <Link to={PATH.MANAGER_PASSWORD_EDIT}>변경하기</Link>
          </Styled.PasswordChangeLinkMessage>
        </Styled.Container> */}
      </Layout>
    </>
  );
};

export default ManagerPasswordEdit;
