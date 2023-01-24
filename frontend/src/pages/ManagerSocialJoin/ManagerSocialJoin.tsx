import { AxiosError } from 'axios';
import { useMutation } from 'react-query';
import { useHistory, useLocation } from 'react-router-dom';
import { postSocialJoin } from 'api/join';
import Header from 'components/Header/Header';
import Layout from 'components/Layout/Layout';
import MESSAGE from 'constants/message';
import PATH from 'constants/path';
import { ErrorResponse } from 'types/response';
import * as Styled from './ManagerSocialJoin.styles';
import SocialJoinForm from './units/SocialJoinForm';

export interface SocialJoinParams {
  emoji: string;
  email: string;
  userName: string;
}

interface SocialJoinState {
  email: string;
  oauthProvider: 'GITHUB' | 'GOOGLE';
}

const ManagerSocialJoin = (): JSX.Element => {
  const history = useHistory();
  const location = useLocation<SocialJoinState>();

  const email = location.state?.email;
  const oauthProvider = location.state?.oauthProvider;

  const socialJoin = useMutation(postSocialJoin, {
    onSuccess: () => {
      history.replace(PATH.LOGIN);
    },

    onError: (error: AxiosError<ErrorResponse>) => {
      alert(error?.response?.data.message ?? MESSAGE.JOIN.FAILURE);
    },
  });

  const handleSubmit = ({ emoji, email, userName }: SocialJoinParams) => {
    if (!emoji || !email || !userName || !oauthProvider || socialJoin.isLoading) return;

    socialJoin.mutate({ emoji, email, userName, oauthProvider });
  };

  if (!email || !oauthProvider) {
    history.replace(PATH.LOGIN);
  }

  return (
    <>
      <Header />
      <Layout>
        <Styled.Container>
          <Styled.PageTitle>추가 정보 입력</Styled.PageTitle>
          <SocialJoinForm email={email} oauthProvider={oauthProvider} onSubmit={handleSubmit} />
        </Styled.Container>
      </Layout>
    </>
  );
};

export default ManagerSocialJoin;
