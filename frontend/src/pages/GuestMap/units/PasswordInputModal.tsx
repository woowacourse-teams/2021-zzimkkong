import React from 'react';
import Button from 'components/Button/Button';
import Input from 'components/Input/Input';
import Modal from 'components/Modal/Modal';
import useInput from 'hooks/useInput';
import * as Styled from './PasswordInputModal.styles';

interface PasswordInputModalProps {
  open: boolean;
  onClose: () => void;
  onSubmit: (passwordInput: string) => void;
}

const PasswordInputModal = ({ open, onClose, onSubmit }: PasswordInputModalProps): JSX.Element => {
  const [passwordInput, onChangePasswordInput] = useInput('');

  const handleSubmit: React.FormEventHandler<HTMLFormElement> = (event) => {
    event.preventDefault();

    onSubmit(passwordInput);
  };

  return (
    <Modal open={open} isClosableDimmer={true} onClose={onClose}>
      <Modal.Header>예약시 사용하신 비밀번호를 입력해주세요.</Modal.Header>
      <Modal.Inner>
        <form onSubmit={handleSubmit}>
          <Input
            type="password"
            label="비밀번호"
            minLength={4}
            maxLength={4}
            value={passwordInput}
            onChange={onChangePasswordInput}
          />
          <Styled.DeleteModalContainer>
            <Button variant="text" type="button" onClick={onClose}>
              취소
            </Button>
            <Button variant="text" type="submit">
              확인
            </Button>
          </Styled.DeleteModalContainer>
        </form>
      </Modal.Inner>
    </Modal>
  );
};

export default PasswordInputModal;
