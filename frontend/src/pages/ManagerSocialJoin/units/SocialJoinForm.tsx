import { AxiosError } from 'axios';
import { FormEventHandler, useState } from 'react';
import { useQuery } from 'react-query';
import { queryValidateUserName } from 'api/join';
import Input from 'components/Input/Input';
import SocialJoinButton from 'components/SocialAuthButton/SocialJoinButton';
import MANAGER from 'constants/manager';
import MESSAGE from 'constants/message';
import useInput from 'hooks/useInput';
import EmojiSelector from 'pages/ManagerJoin/units/EmojiSelector';
import { ErrorResponse } from 'types/response';
import { SocialJoinParams } from '../ManagerSocialJoin';
import * as Styled from './SocialJoinForm.styles';

interface Props {
  email: string;
  oauthProvider: 'GITHUB' | 'GOOGLE';
  onSubmit: ({ emoji, email, userName }: SocialJoinParams) => void;
}

const SocialJoinForm = ({ email, oauthProvider, onSubmit }: Props): JSX.Element => {
  const [emoji, setEmoji] = useState('');
  const [userName, onChangeUserName] = useInput('');

  const [userNameMessage, setUserNameMessage] = useState('');

  const checkValidateUserName = useQuery(
    ['checkValidateUserName', userName],
    queryValidateUserName,
    {
      enabled: false,
      retry: false,

      onSuccess: () => {
        setUserNameMessage(MESSAGE.JOIN.VALID_USERNAME);
      },

      onError: (error: AxiosError<ErrorResponse>) => {
        setUserNameMessage(error.response?.data.message ?? '');
      },
    }
  );

  const handleChangeUserName = (event: React.ChangeEvent<HTMLInputElement>) => {
    onChangeUserName(event);
    setUserNameMessage('');
  };

  const handleValidateUserName = () => {
    if (!userName) return;

    checkValidateUserName.refetch();
  };

  const handleSelectEmoji = (emoji: string) => {
    setEmoji(emoji);
  };

  const handleSubmit: FormEventHandler<HTMLFormElement> = (event) => {
    event.preventDefault();

    if (!emoji || !email || !userName) return;

    onSubmit({ emoji, email, userName });
  };

  return (
    <Styled.Form onSubmit={handleSubmit}>
      <EmojiSelector onSelect={handleSelectEmoji} />
      <Styled.InputWrapper>
        <Input type="email" label="이메일" name="email" value={email} required disabled />
      </Styled.InputWrapper>
      <Styled.InputWrapper>
        <Input
          type="text"
          label="이름"
          name="userName"
          minLength={MANAGER.USERNAME.MIN_LENGTH}
          maxLength={MANAGER.USERNAME.MAX_LENGTH}
          value={userName}
          onChange={handleChangeUserName}
          onBlur={handleValidateUserName}
          message={userNameMessage}
          status={checkValidateUserName.isSuccess ? 'success' : 'error'}
          required
          autoFocus
        />
      </Styled.InputWrapper>
      <SocialJoinButton provider={oauthProvider} />
    </Styled.Form>
  );
};

export default SocialJoinForm;
