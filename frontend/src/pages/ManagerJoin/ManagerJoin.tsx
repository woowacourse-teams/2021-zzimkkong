import { AxiosError } from 'axios';
import React from 'react';
import { useMutation } from 'react-query';
import { useHistory } from 'react-router';
import { Link } from 'react-router-dom';
import { postJoin } from 'api/join';
import Header from 'components/Header/Header';
import Layout from 'components/Layout/Layout';
import MESSAGE from 'constants/message';
import PATH from 'constants/path';
import { ErrorResponse } from 'types/response';
import * as Styled from './ManagerJoin.styles';
import JoinForm from './units/JoinForm';

export interface JoinParams {
  emoji: string;
  email: string;
  password: string;
  userName: string;
}

const ManagerJoin = (): JSX.Element => {
  const history = useHistory();

  const join = useMutation(postJoin, {
    onSuccess: () => {
      alert(MESSAGE.JOIN.SUCCESS);
      history.push(PATH.LOGIN);
    },

    onError: (error: AxiosError<ErrorResponse>) => {
      alert(error?.response?.data.message ?? MESSAGE.JOIN.FAILURE);
    },
  });

  const handleSubmit = ({ emoji, email, password, userName }: JoinParams) => {
    if (!emoji || !email || !password || !userName) return;

    join.mutate({ emoji, email, password, userName });
  };

  return (
    <>
      <Header />
      <Layout>
        <Styled.Container>
          <Styled.PageTitle>회원가입</Styled.PageTitle>
          <JoinForm onSubmit={handleSubmit} />
          <Styled.JoinLinkMessage>
            이미 회원이신가요?
            <Link to={PATH.LOGIN}>로그인하기</Link>
          </Styled.JoinLinkMessage>
        </Styled.Container>
      </Layout>
    </>
  );
};

export default ManagerJoin;
