import { ChangeEventHandler, FormEventHandler } from 'react';
import Button from 'components/Button/Button';
import Input from 'components/Input/Input';
import Modal from 'components/Modal/Modal';
import SPACE from 'constants/space';
import * as Styled from './PresetNameModal.styles';

interface Props {
  open: boolean;
  onClose: () => void;
  onSubmit: FormEventHandler<HTMLFormElement>;
  onChange: ChangeEventHandler<HTMLInputElement>;
  presetName: string;
  errorMessage?: string;
}

const PresetNameModal = ({
  open,
  onClose,
  onSubmit,
  onChange,
  presetName,
  errorMessage,
}: Props): JSX.Element => {
  return (
    <Modal open={open} onClose={onClose} isClosableDimmer showCloseButton>
      <Modal.Header>추가할 프리셋의 이름을 입력해주세요</Modal.Header>
      <Modal.Inner>
        <form onSubmit={onSubmit}>
          <Input
            type="text"
            maxLength={SPACE.PRESET_NAME.MAX_LENGTH}
            value={presetName}
            onChange={onChange}
            message={errorMessage ?? ''}
            status={!!errorMessage ? 'error' : 'default'}
            autoFocus
          />
          <Styled.PresetNameFormControl>
            <Button variant="text">저장</Button>
          </Styled.PresetNameFormControl>
        </form>
      </Modal.Inner>
    </Modal>
  );
};

export default PresetNameModal;
