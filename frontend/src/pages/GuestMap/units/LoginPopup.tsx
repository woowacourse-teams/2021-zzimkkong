import { AxiosError, AxiosResponse } from 'axios';
import { useContext, useState } from 'react';
import { useMutation } from 'react-query';
import { useHistory } from 'react-router-dom';
import { postLogin } from 'api/login';
import Button from 'components/Button/Button';
import Input from 'components/Input/Input';
import Modal from 'components/Modal/Modal';
import SocialLoginButton from 'components/SocialAuthButton/SocialLoginButton';
import MANAGER from 'constants/manager';
import MESSAGE from 'constants/message';
import useInputs from 'hooks/useInputs';
import { AccessTokenContext } from 'providers/AccessTokenProvider';
import { ErrorResponse, LoginSuccess } from 'types/response';
import * as Styled from './LoginPopup.styles';

interface LoginPopupProps {
  open: boolean;
  onClose: () => void;
}

const LoginPopup = ({ open, onClose }: LoginPopupProps): JSX.Element => {
  const { setAccessToken } = useContext(AccessTokenContext);

  const history = useHistory();

  const [{ email, password }, onChangeForm, setValues] = useInputs<{
    email: string;
    password: string;
  }>({
    email: '',
    password: '',
  });

  const [loginErrorMessage, setLoginErrorMessage] = useState<{ email?: string; password?: string }>(
    {
      email: undefined,
      password: undefined,
    }
  );

  const login = useMutation(postLogin, {
    onSuccess: (response: AxiosResponse<LoginSuccess>) => {
      const { accessToken } = response.data;

      setAccessToken(accessToken);
      setValues({ email: '', password: '' });
    },
    onError: (error: AxiosError<ErrorResponse>) => {
      const field = error.response?.data.field;
      const message = error.response?.data.message;

      if (field && message) {
        setLoginErrorMessage({ [field]: message });
        return;
      }

      setLoginErrorMessage({ password: message ?? MESSAGE.LOGIN.UNEXPECTED_ERROR });
    },
  });

  const handleSubmitLogin: React.FormEventHandler<HTMLFormElement> = (event) => {
    event.preventDefault();

    login.mutate({ email, password });
  };

  return (
    <Modal open={open} onClose={onClose}>
      <Styled.LoginPopupWrapper>
        <Styled.LoginPopupHeading>
          <strong>나의 예약 내역</strong>을 관리해보세요!
        </Styled.LoginPopupHeading>
        <Styled.LoginPopupForm onSubmit={handleSubmitLogin}>
          <Styled.LoginFormInputWrapper>
            <Input
              type="email"
              label="이메일"
              name="email"
              value={email}
              onChange={onChangeForm}
              message={loginErrorMessage?.email}
              status={loginErrorMessage?.email ? 'error' : 'default'}
              autoFocus
              required
            />
            <Input
              type="password"
              label="비밀번호"
              name="password"
              value={password}
              minLength={MANAGER.PASSWORD.MIN_LENGTH}
              maxLength={MANAGER.PASSWORD.MAX_LENGTH}
              onChange={onChangeForm}
              message={loginErrorMessage?.password}
              status={loginErrorMessage?.password ? 'error' : 'default'}
              required
            />
          </Styled.LoginFormInputWrapper>
          <Styled.LoginFormButtonWrapper>
            <Button type="submit" variant="primary" size="medium" fullWidth>
              로그인
            </Button>
            <Button
              type="button"
              variant="inverse"
              size="medium"
              fullWidth
              onClick={() => history.push('/join')}
            >
              회원가입
            </Button>
          </Styled.LoginFormButtonWrapper>
        </Styled.LoginPopupForm>
        <Styled.Line />
        <Styled.SocialLoginButtonWrapper>
          <SocialLoginButton provider="GITHUB" variant="icon" />
          <SocialLoginButton provider="GOOGLE" variant="icon" />
        </Styled.SocialLoginButtonWrapper>
        <Styled.ContinueWithNonMemberWrapper>
          <Styled.ContinueWithNonMember onClick={onClose}>
            비회원으로 계속하기
          </Styled.ContinueWithNonMember>
        </Styled.ContinueWithNonMemberWrapper>
      </Styled.LoginPopupWrapper>
    </Modal>
  );
};

export default LoginPopup;
