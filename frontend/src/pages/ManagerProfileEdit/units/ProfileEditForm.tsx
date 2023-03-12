import { AxiosError } from 'axios';
import React, { useEffect, useState } from 'react';
import { useQuery } from 'react-query';
import { useHistory } from 'react-router-dom';
import { queryValidateUserName } from 'api/join';
import Button from 'components/Button/Button';
import EmojiSelector from 'components/EmojiSelector/EmojiSelector';
import Input from 'components/Input/Input';
import MANAGER from 'constants/manager';
import MESSAGE from 'constants/message';
import useMember from 'hooks/query/useMember';
import useInputs from 'hooks/useInputs';
import { ErrorResponse } from 'types/response';
import * as Styled from './ProfileEditForm.styles';

interface ProfileEditFormProps {
  onSubmit: ({ userName, emoji }: { userName: string; emoji: string }) => void;
}

const ProfileEditForm = ({ onSubmit }: ProfileEditFormProps) => {
  const history = useHistory();

  const member = useMember();
  const initialUserName = member.data?.data.userName;
  const initialEmoji = member.data?.data.emoji.name;

  const [emoji, setEmoji] = useState<string>('');
  const [{ userName }, onChangeForm, setInputs] = useInputs<{ userName: string }>({
    userName: '',
  });

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
        setUserNameMessage(
          error.response?.data.message ?? MESSAGE.JOIN.CHECK_USERNAME_UNEXPECTED_ERROR
        );
      },
    }
  );

  const handleSelectEmoji = (emoji: string) => {
    setEmoji(emoji);
  };

  const handleChangeUserName = (event: React.ChangeEvent<HTMLInputElement>) => {
    onChangeForm(event);
    setUserNameMessage('');
  };

  const handleSubmit: React.FormEventHandler<HTMLFormElement> = (event) => {
    event.preventDefault();

    onSubmit({ userName, emoji });
  };

  const isSubmitButtonDisabled = !(emoji && userName);

  const handleCancel = () => {
    history.goBack();
  };

  useEffect(() => {
    if (!initialUserName) return;

    setInputs((prevInputs) => ({
      ...prevInputs,
      userName: initialUserName,
    }));
  }, [initialUserName, setInputs]);

  useEffect(() => {
    if (!initialEmoji) return;

    setEmoji(initialEmoji);
  }, [initialEmoji]);

  return (
    <Styled.Form onSubmit={handleSubmit}>
      {initialEmoji && <EmojiSelector initialEmoji={initialEmoji} onSelect={handleSelectEmoji} />}

      {initialUserName && (
        <Styled.InputWrapper>
          <Input
            type="text"
            label="이름"
            name="userName"
            minLength={MANAGER.USERNAME.MIN_LENGTH}
            maxLength={MANAGER.USERNAME.MAX_LENGTH}
            value={userName}
            onChange={handleChangeUserName}
            message={userNameMessage}
            status={checkValidateUserName.isSuccess ? 'success' : 'error'}
            required
          />
        </Styled.InputWrapper>
      )}
      <Styled.ButtonContainer>
        <Button size="large" type="button" fullWidth onClick={handleCancel}>
          취소
        </Button>
        <Button variant="primary" size="large" fullWidth disabled={isSubmitButtonDisabled}>
          수정
        </Button>
      </Styled.ButtonContainer>
    </Styled.Form>
  );
};

export default ProfileEditForm;
