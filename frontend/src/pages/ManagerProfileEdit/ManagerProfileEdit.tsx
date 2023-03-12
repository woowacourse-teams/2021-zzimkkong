import { AxiosError } from 'axios';
import React from 'react';
import { useMutation } from 'react-query';
import { Link, useHistory } from 'react-router-dom';
import { putMember } from 'api/member';
import Header from 'components/Header/Header';
import Layout from 'components/Layout/Layout';
import MESSAGE from 'constants/message';
import PATH from 'constants/path';
import { ErrorResponse } from 'types/response';
import * as Styled from './ManagerProfileEdit.styles';
import ProfileEditForm from './units/ProfileEditForm';

const ManagerProfileEdit = () => {
  const history = useHistory();

  const editProfile = useMutation(putMember, {
    onSuccess: () => {
      history.push(PATH.MANAGER_MAP_LIST);
    },

    onError: (error: AxiosError<ErrorResponse>) => {
      alert(error?.response?.data.message ?? MESSAGE.MEMBER.EDIT_PROFILE_UNEXPECTED_ERROR);
    },
  });

  const handleSubmit = ({ userName, emoji }: { userName: string; emoji: string }) => {
    editProfile.mutate({ userName, emoji });
  };

  return (
    <>
      <Header />
      <Layout>
        <Styled.Container>
          <Styled.PageTitle>내 정보 수정</Styled.PageTitle>
          <ProfileEditForm onSubmit={handleSubmit} />
          <Styled.PasswordChangeLinkMessage>
            비밀번호를 변경하고 싶으신가요?
            <Link to="/">변경하기</Link>
          </Styled.PasswordChangeLinkMessage>
        </Styled.Container>
      </Layout>
    </>
  );
};

export default ManagerProfileEdit;
